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

    private static final Path BASE_DIR = resolveBaseDir();
    private static final Path USERS_FILE = BASE_DIR.resolve("ta_users.csv");
    private static final Path JOBS_FILE = BASE_DIR.resolve("jobs.csv");
    private static final Path APPLICATIONS_FILE = BASE_DIR.resolve("applications.csv");

    static {
        initFiles();
    }

    private static Path resolveBaseDir() {
        Path repoDataDir = resolveRepoDataDir();
        if (repoDataDir != null) {
            return repoDataDir;
        }

        List<Path> candidates = new ArrayList<>();

        String sysProp = System.getProperty("ta.data.dir");
        if (sysProp != null && !sysProp.isBlank()) {
            candidates.add(Paths.get(sysProp));
        }

        String env = System.getenv("TA_DATA_DIR");
        if (env != null && !env.isBlank()) {
            candidates.add(Paths.get(env));
        }

        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.isBlank()) {
            candidates.add(Paths.get(catalinaBase, "ta-data"));
        }

        String userDir = System.getProperty("user.dir");
        if (userDir != null && !userDir.isBlank()) {
            Path ud = Paths.get(userDir);
            candidates.add(ud.resolve("data"));
            candidates.add(ud.resolve("ta-webapp").resolve("data"));
            if (ud.getParent() != null) {
                candidates.add(ud.getParent().resolve("data"));
            }
        }

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(0);
        }

        return Paths.get("data");
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
            // Fallback to legacy resolution flow if code source cannot be resolved.
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
            ensureFile(USERS_FILE, USERS_HEADER);
            ensureFile(JOBS_FILE, JOBS_HEADER);
            ensureFile(APPLICATIONS_FILE, APPLICATIONS_HEADER);
            ensureDefaultUsers();
            ensureDefaultJobs();
            ensureDefaultApplications();
        } catch (IOException e) {
            throw new RuntimeException("初始化数据文件失败：" + e.getMessage(), e);
        }
    }

    private static void ensureDefaultUsers() throws IOException {
        List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            try (BufferedWriter writer = Files.newBufferedWriter(USERS_FILE, StandardCharsets.UTF_8)) {
                writer.write(USERS_HEADER);
                writer.newLine();
                writer.write("U001,seele,123456,Seele,seele@bupt.edu.cn,TA,3,IoT,Java|Python|Data Structure|STM32,ACTIVE,");
                writer.newLine();
                writer.write("U002,mo1,123456,Dr.Wang,wang@bupt.edu.cn,MO,0,Faculty,Teaching|Java,ACTIVE,");
                writer.newLine();
                writer.write("U003,admin,123456,System Admin,admin@bupt.edu.cn,ADMIN,0,Office,Management,ACTIVE,");
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
                writer.write("J003,Data Structures TA,EBU6102,Dr.Zhao,1,4,16,OPEN,Java|Data Structure|Communication,92,2026-04-30,4");
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
                writer.write("A001,U001,J001,PENDING,2026-03-16 10:00:00,First application,");
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
        } catch (IOException e) {
            throw new RuntimeException("读取用户数据失败：" + e.getMessage(), e);
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
        } catch (IOException e) {
            throw new RuntimeException("保存用户数据失败：" + e.getMessage(), e);
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
        } catch (IOException e) {
            throw new RuntimeException("读取岗位数据失败：" + e.getMessage(), e);
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
        } catch (IOException e) {
            throw new RuntimeException("保存岗位数据失败：" + e.getMessage(), e);
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
        } catch (IOException e) {
            throw new RuntimeException("读取申请数据失败：" + e.getMessage(), e);
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
        } catch (IOException e) {
            throw new RuntimeException("保存申请数据失败：" + e.getMessage(), e);
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