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

    private static final Path REPO_DATA_DIR = resolveRepoDataDir();
    private static final Path BASE_DIR = resolveBaseDir();
    private static final Path MIRROR_DIR = resolveMirrorDir();

    private static final Path USERS_FILE = BASE_DIR.resolve("ta_users.csv");
    private static final Path JOBS_FILE = BASE_DIR.resolve("jobs.csv");
    private static final Path APPLICATIONS_FILE = BASE_DIR.resolve("applications.csv");

    static {
        initFiles();
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
        if (REPO_DATA_DIR == null) {
            return null;
        }

        Path repoDir = REPO_DATA_DIR.toAbsolutePath().normalize();
        return repoDir.equals(BASE_DIR) ? null : repoDir;
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
            // Fall back to relative data directory if repo path cannot be resolved.
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
            throw new RuntimeException("初始化数据文件失败: " + e.getMessage(), e);
        }
    }

    private static void ensureDefaultUsers() throws IOException {
        List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            try (BufferedWriter writer = Files.newBufferedWriter(USERS_FILE, StandardCharsets.UTF_8)) {
                writer.write(USERS_HEADER);
                writer.newLine();
                writer.write("U001,seele,123456,Seele,seele@bupt.edu.cn,TA,3,IoT,Java|Python|Data Structure|STM32,ACTIVE,Mon/Wed afternoons");
                writer.newLine();
                writer.write("U002,luna,123456,Luna,luna@bupt.edu.cn,TA,2,Software Engineering,Java|Testing|Documentation,ACTIVE,Tue/Thu mornings");
                writer.newLine();
                writer.write("U003,kevin,123456,Kevin,kevin@bupt.edu.cn,TA,4,Embedded Systems,C|STM32|Debugging,ACTIVE,Fri all day");
                writer.newLine();
                writer.write("U004,mo1,123456,Dr.Wang,wang@bupt.edu.cn,MO,0,Faculty,Teaching|Java,ACTIVE,");
                writer.newLine();
                writer.write("U005,mo2,123456,Dr.Liu,liu@bupt.edu.cn,MO,0,Faculty,C|Circuits|Lab Supervision,ACTIVE,");
                writer.newLine();
                writer.write("U006,admin,123456,System Admin,admin@bupt.edu.cn,ADMIN,0,Office,Management,ACTIVE,");
                writer.newLine();
            }
        }
    }

    private static void ensureDefaultJobs() throws IOException {
        List<String> lines = Files.readAllLines(JOBS_FILE, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            try (BufferedWriter writer = Files.newBufferedWriter(JOBS_FILE, StandardCharsets.UTF_8)) {
                writer.write(JOBS_HEADER);
                writer.newLine();
                writer.write("J001,Software Engineering TA,EBU6304,Dr.Wang,2,4,20,OPEN,Java|Teamwork|Documentation,95,2026-05-01,3");
                writer.newLine();
                writer.write("J002,Embedded Systems TA,EBU6201,Dr.Liu,2,4,18,OPEN,C|STM32|Debugging,89,2026-05-15,2");
                writer.newLine();
                writer.write("J003,Data Structures TA,EBU6102,Dr.Wang,1,4,16,OPEN,Java|Data Structure|Communication,92,2026-04-30,2");
                writer.newLine();
                writer.write("J004,Digital Systems Lab TA,EBU6204,Dr.Liu,2,4,12,OPEN,Circuits|Lab Support|Communication,84,2026-05-20,2");
                writer.newLine();
            }
        }
    }

    private static void ensureDefaultApplications() throws IOException {
        List<String> lines = Files.readAllLines(APPLICATIONS_FILE, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            try (BufferedWriter writer = Files.newBufferedWriter(APPLICATIONS_FILE, StandardCharsets.UTF_8)) {
                writer.write(APPLICATIONS_HEADER);
                writer.newLine();
                writer.write("A001,U001,J001,PENDING,2026-03-16 10:00:00,First application,Mon/Wed afternoons");
                writer.newLine();
            }
        }
    }

    private static void ensureFile(Path file, String header) throws IOException {
        if (!Files.exists(file)) {
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                writer.write(header);
                writer.newLine();
            }
        }
    }

    private static void bootstrapFile(Path primary, Path mirror, String header) throws IOException {
        if (!Files.exists(primary)) {
            if (mirror != null && Files.exists(mirror)) {
                Files.copy(mirror, primary, StandardCopyOption.REPLACE_EXISTING);
            } else {
                ensureFile(primary, header);
            }
        }

        if (mirror != null && !Files.exists(mirror)) {
            Files.copy(primary, mirror, StandardCopyOption.REPLACE_EXISTING);
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

        boolean shouldCopy = !Files.exists(mirror)
                || Files.size(sourceFile) != Files.size(mirror)
                || Files.getLastModifiedTime(sourceFile).compareTo(Files.getLastModifiedTime(mirror)) > 0;

        if (shouldCopy) {
            Files.copy(sourceFile, mirror, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public synchronized List<User> loadUsers() {
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
            syncFileToMirror(USERS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("读取用户数据失败: " + e.getMessage(), e);
        }
        return users;
    }

    public synchronized void saveUsers(List<User> users) {
        try (BufferedWriter writer = Files.newBufferedWriter(USERS_FILE, StandardCharsets.UTF_8)) {
            writer.write(USERS_HEADER);
            writer.newLine();
            for (User user : users) {
                writer.write(toCsv(
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
                writer.newLine();
            }
            syncFileToMirror(USERS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("保存用户数据失败: " + e.getMessage(), e);
        }
    }

    public synchronized List<Job> loadJobs() {
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
                    continue;
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
            syncFileToMirror(JOBS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("读取岗位数据失败: " + e.getMessage(), e);
        }
        return jobs;
    }

    public synchronized void saveJobs(List<Job> jobs) {
        try (BufferedWriter writer = Files.newBufferedWriter(JOBS_FILE, StandardCharsets.UTF_8)) {
            writer.write(JOBS_HEADER);
            writer.newLine();
            for (Job job : jobs) {
                writer.write(toCsv(
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
                writer.newLine();
            }
            syncFileToMirror(JOBS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("保存岗位数据失败: " + e.getMessage(), e);
        }
    }

    public synchronized List<Application> loadApplications() {
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
            syncFileToMirror(APPLICATIONS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("读取申请数据失败: " + e.getMessage(), e);
        }
        return apps;
    }

    public synchronized void saveApplications(List<Application> apps) {
        try (BufferedWriter writer = Files.newBufferedWriter(APPLICATIONS_FILE, StandardCharsets.UTF_8)) {
            writer.write(APPLICATIONS_HEADER);
            writer.newLine();
            for (Application app : apps) {
                writer.write(toCsv(
                        app.getApplicationId(),
                        app.getUserId(),
                        app.getJobId(),
                        app.getStatus() == null ? "PENDING" : app.getStatus().name(),
                        app.getSubmittedAt(),
                        app.getNotes() == null ? "" : app.getNotes(),
                        app.getAvailability() == null ? "" : app.getAvailability()
                ));
                writer.newLine();
            }
            syncFileToMirror(APPLICATIONS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("保存申请数据失败: " + e.getMessage(), e);
        }
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
