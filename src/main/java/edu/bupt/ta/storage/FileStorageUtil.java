package edu.bupt.ta.storage;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileStorageUtil {

    private static final String USERS_HEADER =
            "userId,username,password,name,email,role,year,major,skills,status,availability";
    private static final String JOBS_HEADER =
            "jobId,title,moduleCode,organiser,minYear,maxYear,hours,status,requiredSkills,matchScore,deadline,vacancies";
    private static final String APPLICATIONS_HEADER =
            "applicationId,userId,jobId,status,submittedAt,notes,availability";

    private static final String DATA_DIR_PROPERTY = "ta.data.dir";
    private static final String DATA_DIR_ENV = "TA_DATA_DIR";
    private static final String MIRROR_DIR_PROPERTY = "ta.data.mirror.dir";
    private static final String MIRROR_DIR_ENV = "TA_DATA_MIRROR_DIR";

    /**
     * Single-process lock.
     * This prevents concurrent requests in the same Tomcat instance from writing CSV files out of order.
     */
    private static final Object IO_LOCK = new Object();

    private static final Path REPO_DATA_DIR = resolveRepoDataDir();
    private static final Path BASE_DIR = resolveBaseDir();
    private static final Path MIRROR_DIR = resolveMirrorDir();

    private static final Path USERS_FILE = BASE_DIR.resolve("ta_users.csv");
    private static final Path JOBS_FILE = BASE_DIR.resolve("jobs.csv");
    private static final Path APPLICATIONS_FILE = BASE_DIR.resolve("applications.csv");

    static {
        initFiles();
    }

    public static FileStorageUtil getInstance() {
        return new FileStorageUtil();
    }

    public Path getBaseDir() {
        return BASE_DIR;
    }

    private static Path resolveBaseDir() {
        String configured = System.getProperty(DATA_DIR_PROPERTY);
        if (configured == null || configured.isBlank()) {
            configured = System.getenv(DATA_DIR_ENV);
        }

        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }

        if (REPO_DATA_DIR != null) {
            return REPO_DATA_DIR.toAbsolutePath().normalize();
        }

        return Paths.get("data").toAbsolutePath().normalize();
    }

    private static Path resolveMirrorDir() {
        String configured = System.getProperty(MIRROR_DIR_PROPERTY);
        if (configured == null || configured.isBlank()) {
            configured = System.getenv(MIRROR_DIR_ENV);
        }

        Path mirror = null;
        if (configured != null && !configured.isBlank()) {
            mirror = Paths.get(configured).toAbsolutePath().normalize();
        } else if (REPO_DATA_DIR != null) {
            mirror = REPO_DATA_DIR.toAbsolutePath().normalize();
        }

        if (mirror == null || mirror.equals(BASE_DIR)) {
            return null;
        }
        return mirror;
    }

    private static Path resolveRepoDataDir() {
        String userDir = System.getProperty("user.dir");
        if (userDir != null && !userDir.isBlank()) {
            Path fromUserDir = findTrackedDataDir(Paths.get(userDir));
            if (fromUserDir != null) {
                return fromUserDir;
            }
        }

        try {
            URI codeSource = FileStorageUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path location = Paths.get(codeSource);
            Path fromCodeSource = findTrackedDataDir(location);
            if (fromCodeSource != null) {
                return fromCodeSource;
            }
        } catch (URISyntaxException | RuntimeException ignored) {
            // Fallback to configured mirror dir or relative data directory.
        }

        return null;
    }

    private static Path findTrackedDataDir(Path start) {
        if (start == null) {
            return null;
        }

        Path current = start.toAbsolutePath().normalize();
        if (Files.isRegularFile(current)) {
            current = current.getParent();
        }

        while (current != null) {
            Path localPom = current.resolve("pom.xml");
            Path localData = current.resolve("data");
            if (Files.exists(localPom) && Files.isDirectory(localData)) {
                return localData;
            }

            Path modulePom = current.resolve("ta-webapp").resolve("pom.xml");
            Path moduleData = current.resolve("ta-webapp").resolve("data");
            if (Files.exists(modulePom) && Files.isDirectory(moduleData)) {
                return moduleData;
            }

            current = current.getParent();
        }

        return null;
    }

    private static void initFiles() {
        synchronized (IO_LOCK) {
            try {
                Files.createDirectories(BASE_DIR);
                if (MIRROR_DIR != null) {
                    Files.createDirectories(MIRROR_DIR);
                }

                bootstrapFile(USERS_FILE, mirrorFile(USERS_FILE), USERS_HEADER);
                bootstrapFile(JOBS_FILE, mirrorFile(JOBS_FILE), JOBS_HEADER);
                bootstrapFile(APPLICATIONS_FILE, mirrorFile(APPLICATIONS_FILE), APPLICATIONS_HEADER);

                ensureDefaultUsers();
                ensureDefaultJobs();
                ensureDefaultApplications();
                syncBaseToMirror();
            } catch (IOException e) {
                throw new RuntimeException("failed to initialize files: " + e.getMessage(), e);
            }
        }
    }

    private static void ensureDefaultUsers() throws IOException {
        List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            List<String> defaultLines = new ArrayList<>();
            defaultLines.add(USERS_HEADER);
            defaultLines.add("U001,seele,123456,Seele,seele@bupt.edu.cn,TA,3,IoT,Java|Python|Data Structure|STM32,ACTIVE,Mon/Wed afternoons");
            defaultLines.add("U002,luna,123456,Luna,luna@bupt.edu.cn,TA,2,Software Engineering,Java|Testing|Documentation,ACTIVE,Tue/Thu mornings");
            defaultLines.add("U003,kevin,123456,Kevin,kevin@bupt.edu.cn,TA,4,Embedded Systems,C|STM32|Debugging,ACTIVE,Fri all day");
            defaultLines.add("U004,mo1,123456,Dr.Wang,wang@bupt.edu.cn,MO,0,Faculty,Teaching|Java,ACTIVE,");
            defaultLines.add("U005,mo2,123456,Dr.Liu,liu@bupt.edu.cn,MO,0,Faculty,C|Circuits|Lab Supervision,ACTIVE,");
            defaultLines.add("U006,admin,123456,System Admin,admin@bupt.edu.cn,ADMIN,0,Office,Management,ACTIVE,");
            writeLinesAtomically(USERS_FILE, defaultLines);
        }
    }

    private static void ensureDefaultJobs() throws IOException {
        List<String> lines = Files.readAllLines(JOBS_FILE, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            List<String> defaultLines = new ArrayList<>();
            defaultLines.add(JOBS_HEADER);
            defaultLines.add("J001,Software Engineering TA,EBU6304,Dr.Wang,2,4,20,OPEN,Java|Teamwork|Documentation,95,2026-05-01,3");
            defaultLines.add("J002,Embedded Systems TA,EBU6201,Dr.Liu,2,4,18,OPEN,C|STM32|Debugging,89,2026-05-15,2");
            defaultLines.add("J003,Data Structures TA,EBU6102,Dr.Wang,1,4,16,OPEN,Java|Data Structure|Communication,92,2026-04-30,2");
            defaultLines.add("J004,Digital Systems Lab TA,EBU6204,Dr.Liu,2,4,12,OPEN,Circuits|Lab Support|Communication,84,2026-05-20,2");
            writeLinesAtomically(JOBS_FILE, defaultLines);
        }
    }

    private static void ensureDefaultApplications() throws IOException {
        List<String> lines = Files.readAllLines(APPLICATIONS_FILE, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            List<String> defaultLines = new ArrayList<>();
            defaultLines.add(APPLICATIONS_HEADER);
            defaultLines.add("A001,U001,J001,PENDING,2026-03-16 10:00:00,First application,Mon/Wed afternoons");
            writeLinesAtomically(APPLICATIONS_FILE, defaultLines);
        }
    }

    private static void ensureFile(Path file, String header) throws IOException {
        if (!Files.exists(file)) {
            List<String> lines = new ArrayList<>();
            lines.add(header);
            writeLinesAtomically(file, lines);
        }
    }

    private static void bootstrapFile(Path primary, Path mirror, String header) throws IOException {
        if (!Files.exists(primary)) {
            if (mirror != null && Files.exists(mirror)) {
                copyFileAtomically(mirror, primary);
            } else {
                ensureFile(primary, header);
            }
        }

        if (mirror != null && !Files.exists(mirror)) {
            copyFileAtomically(primary, mirror);
        }
    }

    private static Path mirrorFile(Path sourceFile) {
        if (MIRROR_DIR == null) {
            return null;
        }
        return MIRROR_DIR.resolve(sourceFile.getFileName().toString());
    }

    private static void syncBaseToMirror() throws IOException {
        syncFileToMirror(USERS_FILE);
        syncFileToMirror(JOBS_FILE);
        syncFileToMirror(APPLICATIONS_FILE);
    }

    private static void syncFileToMirror(Path sourceFile) throws IOException {
        Path mirror = mirrorFile(sourceFile);
        if (mirror == null || !Files.exists(sourceFile)) {
            return;
        }
        copyFileAtomically(sourceFile, mirror);
    }

    private static void writeLinesAtomically(Path target, List<String> lines) throws IOException {
        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }

        moveAtomically(temp, target);
    }

    private static void copyFileAtomically(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".tmp");
        Files.copy(source, temp, StandardCopyOption.REPLACE_EXISTING);
        moveAtomically(temp, target);
    }

    private static void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public List<User> loadUsers() {
        synchronized (IO_LOCK) {
            List<User> users = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(USERS_FILE, StandardCharsets.UTF_8)) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    if (line.isBlank()) {
                        continue;
                    }
                    List<String> f = parseCsvLine(line);
                    if (f.size() < 11) {
                        continue;
                    }
                    User user = new User(
                            f.get(0),
                            f.get(1),
                            f.get(2),
                            f.get(3),
                            f.get(4),
                            UserRole.fromString(f.get(5)),
                            parseInt(f.get(6), 0),
                            f.get(7),
                            f.get(8),
                            f.get(9),
                            f.get(10)
                    );
                    users.add(user);
                }
            } catch (IOException e) {
                throw new RuntimeException("failed to load users data: " + e.getMessage(), e);
            }
            return users;
        }
    }

    public void saveUsers(List<User> users) {
        synchronized (IO_LOCK) {
            try {
                List<String> lines = new ArrayList<>();
                lines.add(USERS_HEADER);
                for (User user : users) {
                    lines.add(toCsv(
                            user.getUserId(),
                            user.getUsername(),
                            user.getPassword(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole() == null ? "TA" : user.getRole().name(),
                            String.valueOf(user.getYear()),
                            user.getMajor(),
                            user.getSkills(),
                            user.getStatus(),
                            user.getAvailability() == null ? "" : user.getAvailability()
                    ));
                }
                writeLinesAtomically(USERS_FILE, lines);
                syncFileToMirror(USERS_FILE);
            } catch (IOException e) {
                throw new RuntimeException("failed to save users data: " + e.getMessage(), e);
            }
        }
    }

    public List<Job> loadJobs() {
        synchronized (IO_LOCK) {
            List<Job> jobs = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(JOBS_FILE, StandardCharsets.UTF_8)) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    if (line.isBlank()) {
                        continue;
                    }
                    List<String> f = parseCsvLine(line);
                    if (f.size() < 12) {
                        // For backward compatibility with old data
                        while (f.size() < 12) {
                            f.add("");
                        }
                    }
                    Job job = new Job(
                            f.get(0),
                            f.get(1),
                            f.get(2),
                            f.get(3),
                            parseInt(f.get(4), 1),
                            parseInt(f.get(5), 4),
                            parseInt(f.get(6), 0),
                            JobStatus.fromString(f.get(7)),
                            f.get(8),
                            parseInt(f.get(9), 0),
                            f.get(10),
                            parseInt(f.get(11), 1)
                    );
                    jobs.add(job);
                }
            } catch (IOException e) {
                throw new RuntimeException("failed to load jobs data: " + e.getMessage(), e);
            }
            return jobs;
        }
    }

    public void saveJobs(List<Job> jobs) {
        synchronized (IO_LOCK) {
            try {
                List<String> lines = new ArrayList<>();
                lines.add(JOBS_HEADER);
                for (Job job : jobs) {
                    lines.add(toCsv(
                            job.getJobId(),
                            job.getTitle(),
                            job.getModuleCode(),
                            job.getOrganiser(),
                            String.valueOf(job.getMinYear()),
                            String.valueOf(job.getMaxYear()),
                            String.valueOf(job.getHours()),
                            job.getStatus() == null ? "OPEN" : job.getStatus().name(),
                            job.getRequiredSkills(),
                            String.valueOf(job.getMatchScore()),
                            job.getDeadline() == null ? "" : job.getDeadline(),
                            String.valueOf(job.getVacancies())
                    ));
                }
                writeLinesAtomically(JOBS_FILE, lines);
                syncFileToMirror(JOBS_FILE);
            } catch (IOException e) {
                throw new RuntimeException("failed to save jobs data: " + e.getMessage(), e);
            }
        }
    }

    public List<Application> loadApplications() {
        synchronized (IO_LOCK) {
            List<Application> apps = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(APPLICATIONS_FILE, StandardCharsets.UTF_8)) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    if (line.isBlank()) {
                        continue;
                    }
                    List<String> f = parseCsvLine(line);
                    if (f.size() < 6) {
                        continue;
                    }
                    Application app = new Application(
                            f.get(0),
                            f.get(1),
                            f.get(2),
                            ApplicationStatus.fromString(f.get(3)),
                            f.get(4),
                            f.get(5),
                            f.size() > 6 ? f.get(6) : ""
                    );
                    apps.add(app);
                }
            } catch (IOException e) {
                throw new RuntimeException("failed to load applications data: " + e.getMessage(), e);
            }
            return apps;
        }
    }

    public void saveApplications(List<Application> apps) {
        synchronized (IO_LOCK) {
            try {
                List<String> lines = new ArrayList<>();
                lines.add(APPLICATIONS_HEADER);
                for (Application app : apps) {
                    lines.add(toCsv(
                            app.getApplicationId(),
                            app.getUserId(),
                            app.getJobId(),
                            app.getStatus() == null ? "PENDING" : app.getStatus().name(),
                            app.getSubmittedAt(),
                            app.getNotes() == null ? "" : app.getNotes(),
                            app.getAvailability() == null ? "" : app.getAvailability()
                    ));
                }
                writeLinesAtomically(APPLICATIONS_FILE, lines);
                syncFileToMirror(APPLICATIONS_FILE);
            } catch (IOException e) {
                throw new RuntimeException("failed to save applications data: " + e.getMessage(), e);
            }
        }
    }

    public void deleteApplications(List<String> applicationIds) {
        List<Application> apps = loadApplications();
        apps.removeIf(app -> applicationIds.contains(app.getApplicationId()));
        saveApplications(apps);
    }

    public String nowText() {
        return LocalDateTime.now().withNano(0).toString().replace('T', ' ');
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String toCsv(String... values) {
        List<String> escaped = new ArrayList<>();
        for (String v : values) {
            escaped.add(escapeCsv(v));
        }
        return String.join(",", escaped);
    }

    private String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n")) {
            safe = safe.replace("\"", "\"\"");
            return "\"" + safe + "\"";
        }
        return safe;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        result.add(current.toString());
        return result;
    }
}
