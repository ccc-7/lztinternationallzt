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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralised file-based persistence layer for the TA Recruitment System.
 * All CSV read/write for users, jobs, and applications passes through this class.
 * Provides path resolution from system properties, environment variables, or auto-detection;
 * atomic writes via a temp-then-move pattern; single-process synchronisation via a static
 * {@code IO_LOCK}; automatic bootstrap and seeding of empty CSV files; and optional mirroring
 * to a secondary data directory.
 *
 * <p>CSV column positions are fixed and must be kept in sync whenever a field is added.
 *
 * @see edu.bupt.ta.service.UserService
 * @see edu.bupt.ta.service.JobService
 * @see edu.bupt.ta.service.ApplicationService
 */
public class FileStorageUtil {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** CSV column header for ta_users.csv (21 columns). */
    private static final String USERS_HEADER =
            "userId,username,password,name,email,role,year,major,skills,status,availability," +
                    "personalStatement,relevantCourses,projectExperience,preferredRole,summaryStatus," +
                    "cvStoredName,cvOriginalName,cvContentType,cvUploadedAt,cvStatus";

    /** CSV column header for jobs.csv (12 columns). */
    private static final String JOBS_HEADER =
            "jobId,title,moduleCode,organiser,minYear,maxYear,hours,status,requiredSkills,matchScore,deadline,vacancies";

    /** CSV column header for applications.csv (7 columns). */
    private static final String APPLICATIONS_HEADER =
            "applicationId,userId,jobId,status,submittedAt,notes,availability";

    private static final String DATA_DIR_PROPERTY = "ta.data.dir";
    private static final String DATA_DIR_ENV = "TA_DATA_DIR";
    private static final String MIRROR_DIR_PROPERTY = "ta.data.mirror.dir";
    private static final String MIRROR_DIR_ENV = "TA_DATA_MIRROR_DIR";

    /**
     * Lock used to serialise all CSV read and write operations within a single JVM.
     * This prevents concurrent HTTP requests from interleaving file operations.
     */
    private static final Object IO_LOCK = new Object();

    private static final Path REPO_DATA_DIR = resolveRepoDataDir();
    private static final Path DEFAULT_BASE_DIR = resolveBaseDir();
    private static final Path DEFAULT_MIRROR_DIR = resolveMirrorDir();

    // Instance-level paths for testability; initialized to defaults.
    private final Path baseDir;
    private final Path usersFile;
    private final Path jobsFile;
    private final Path applicationsFile;

    // Cached mirror dir for this instance (recomputed per-instance because baseDir may differ).
    private final Path mirrorDir;

    /**
     * Default constructor. Uses the resolved default base directory
     * (system property {@code ta.data.dir} &rarr; env var {@code TA_DATA_DIR}
     * &rarr; auto-detected repository data directory &rarr; relative {@code data} path).
     * Initializes the data directory and CSV files on first instantiation.
     */
    public FileStorageUtil() {
        this(DEFAULT_BASE_DIR, DEFAULT_MIRROR_DIR);
    }

    /**
     * Constructor with explicit base directory. Used by tests to redirect all file I/O
     * to a temporary directory.
     *
     * @param baseDir the directory that should contain {@code ta_users.csv},
     *                {@code jobs.csv}, and {@code applications.csv}
     */
    public FileStorageUtil(Path baseDir, Path mirrorDir) {
        this.baseDir = baseDir.toAbsolutePath().normalize();
        this.mirrorDir = (mirrorDir != null && !mirrorDir.equals(this.baseDir))
                ? mirrorDir.toAbsolutePath().normalize() : null;
        this.usersFile = this.baseDir.resolve("ta_users.csv");
        this.jobsFile = this.baseDir.resolve("jobs.csv");
        this.applicationsFile = this.baseDir.resolve("applications.csv");
        initFiles();
    }

    /**
     * Returns a new FileStorageUtil instance using the default base directory.
     *
     * @return a new FileStorageUtil instance
     */
    public static FileStorageUtil getInstance() {
        return new FileStorageUtil();
    }

    /**
     * Returns the base data directory path for this instance.
     *
     * @return the absolute base directory path
     */
    public Path getBaseDir() {
        return baseDir;
    }

    // ---- Path resolution ----

    /**
     * Resolves the base data directory.
     * Resolution order: system property {@code ta.data.dir} &rarr; env var {@code TA_DATA_DIR}
     * &rarr; auto-detected repository data directory &rarr; relative {@code "data"} path.
     */
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

    /**
     * Resolves the mirror data directory from system property {@code ta.data.mirror.dir}
     * or env var {@code TA_DATA_MIRROR_DIR}. Returns null if mirroring is not configured
     * or if the resolved path equals the base directory.
     */
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

        if (mirror == null || mirror.equals(DEFAULT_BASE_DIR)) {
            return null;
        }
        return mirror;
    }

    /**
     * Auto-detects the data directory by walking up from {@code user.dir} and from the
     * class's code source location, looking for a directory that contains both
     * {@code pom.xml} and a {@code data} subdirectory.
     */
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
        }

        return null;
    }

    /**
     * Walks upward from {@code start} looking for a directory that contains both
     * {@code pom.xml} and a {@code data} subdirectory, or a {@code ta-webapp/pom.xml}
     * and {@code ta-webapp/data} subdirectory.
     */
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

    // ---- Initialisation ----

    /**
     * Initialises the data directory and CSV files on construction.
     * Creates directories, bootstraps missing CSV files with headers, seeds default data
     * if files are empty, and syncs the mirror directory.
     *
     * @throws RuntimeException if any I/O operation fails during initialisation
     */
    private void initFiles() {
        synchronized (IO_LOCK) {
            try {
                Files.createDirectories(baseDir);
                if (mirrorDir != null) {
                    Files.createDirectories(mirrorDir);
                }

                bootstrapFile(usersFile, mirrorFile(usersFile), USERS_HEADER);
                bootstrapFile(jobsFile, mirrorFile(jobsFile), JOBS_HEADER);
                bootstrapFile(applicationsFile, mirrorFile(applicationsFile), APPLICATIONS_HEADER);

                ensureDefaultUsers();
                ensureDefaultJobs();
                ensureDefaultApplications();
                syncBaseToMirror();
            } catch (IOException e) {
                throw new RuntimeException("failed to initialize files: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Seeds ta_users.csv with 3 TAs, 2 MOs, and 1 Admin if the file has only a header
     * row or is empty.
     */
    private void ensureDefaultUsers() throws IOException {
        List<String> lines = Files.readAllLines(usersFile, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            List<String> defaultLines = new ArrayList<>();
            defaultLines.add(USERS_HEADER);
            defaultLines.add(toCsv(
                    "U001", "seele", "123456", "Seele", "seele@bupt.edu.cn", "TA", "3", "IoT",
                    "Java|Python|Data Structure|STM32", "ACTIVE", "Mon/Wed afternoons",
                    "Interested in supporting programming labs and helping junior students debug code.",
                    "EBU6304 Software Engineering|Data Structures|Embedded Systems",
                    "Built Java web applications and STM32-based embedded projects.",
                    "Lab Support|Programming Tutorial", "SUMMARY_COMPLETE",
                    "", "", "", "", "MISSING"));
            defaultLines.add(toCsv(
                    "U002", "luna", "123456", "Luna", "luna@bupt.edu.cn", "TA", "2", "Software Engineering",
                    "Java|Testing|Documentation", "ACTIVE", "Tue/Thu mornings",
                    "Enjoy supporting students through testing, documentation, and peer review.",
                    "Software Testing|Requirements Engineering",
                    "Worked on team coursework documentation and UI validation tasks.",
                    "Tutorial Support|Marking", "SUMMARY_COMPLETE",
                    "", "", "", "", "MISSING"));
            defaultLines.add(toCsv(
                    "U003", "kevin", "123456", "Kevin", "kevin@bupt.edu.cn", "TA", "4", "Embedded Systems",
                    "C|STM32|Debugging", "ACTIVE", "Fri all day",
                    "Focused on embedded debugging and lab support for hardware modules.",
                    "Embedded Systems|Digital Electronics",
                    "Delivered several STM32 projects and hardware troubleshooting demos.",
                    "Lab Support", "SUMMARY_COMPLETE",
                    "", "", "", "", "MISSING"));
            defaultLines.add(toCsv(
                    "U004", "mo1", "123456", "Dr.Wang", "wang@bupt.edu.cn", "MO", "0", "Faculty",
                    "Teaching|Java", "ACTIVE", "", "", "", "", "", "INCOMPLETE",
                    "", "", "", "", "MISSING"));
            defaultLines.add(toCsv(
                    "U005", "mo2", "123456", "Dr.Liu", "liu@bupt.edu.cn", "MO", "0", "Faculty",
                    "C|Circuits|Lab Supervision", "ACTIVE", "", "", "", "", "", "INCOMPLETE",
                    "", "", "", "", "MISSING"));
            defaultLines.add(toCsv(
                    "U006", "admin", "123456", "System Admin", "admin@bupt.edu.cn", "ADMIN", "0", "Office",
                    "Management", "ACTIVE", "", "", "", "", "", "INCOMPLETE",
                    "", "", "", "", "MISSING"));
            writeLinesAtomically(usersFile, defaultLines);
        }
    }

    /**
     * Seeds jobs.csv with 4 sample job postings if the file has only a header row
     * or is empty.
     */
    private void ensureDefaultJobs() throws IOException {
        List<String> lines = Files.readAllLines(jobsFile, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            List<String> defaultLines = new ArrayList<>();
            defaultLines.add(JOBS_HEADER);
            defaultLines.add("J001,Software Engineering TA,EBU6304,Dr.Wang,2,4,20,OPEN,Java|Teamwork|Documentation,95,2026-06-30,3");
            defaultLines.add("J002,Embedded Systems TA,EBU6201,Dr.Liu,2,4,18,OPEN,C|STM32|Debugging,89,2026-06-30,2");
            defaultLines.add("J003,Data Structures TA,EBU6102,Dr.Wang,1,4,16,OPEN,Java|Data Structure|Communication,92,2026-06-30,2");
            defaultLines.add("J004,Digital Systems Lab TA,EBU6204,Dr.Liu,2,4,12,OPEN,Circuits|Lab Support|Communication,84,2026-06-30,2");
            writeLinesAtomically(jobsFile, defaultLines);
        }
    }

    /**
     * Seeds applications.csv with one sample application if the file has only a header
     * row or is empty.
     */
    private void ensureDefaultApplications() throws IOException {
        List<String> lines = Files.readAllLines(applicationsFile, StandardCharsets.UTF_8);
        if (lines.size() <= 1) {
            List<String> defaultLines = new ArrayList<>();
            defaultLines.add(APPLICATIONS_HEADER);
            defaultLines.add("A001,U001,J001,PENDING,2026-03-16 10:00:00,First application,Mon/Wed afternoons");
            writeLinesAtomically(applicationsFile, defaultLines);
        }
    }

    private void ensureFile(Path file, String header) throws IOException {
        if (!Files.exists(file)) {
            List<String> lines = new ArrayList<>();
            lines.add(header);
            writeLinesAtomically(file, lines);
        }
    }

    /**
     * Bootstraps a CSV file: if the primary file is missing, copies from mirror if
     * available, otherwise creates it with a header. Also ensures the mirror is
     * present if the primary exists.
     */
    private void bootstrapFile(Path primary, Path mirror, String header) throws IOException {
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

    /**
     * Maps a source file path to the corresponding path in the mirror directory.
     * Returns null if mirroring is not configured.
     */
    private Path mirrorFile(Path sourceFile) {
        if (mirrorDir == null) {
            return null;
        }
        return mirrorDir.resolve(sourceFile.getFileName().toString());
    }

    private void syncBaseToMirror() throws IOException {
        syncFileToMirror(usersFile);
        syncFileToMirror(jobsFile);
        syncFileToMirror(applicationsFile);
    }

    private void syncFileToMirror(Path sourceFile) throws IOException {
        Path mirror = mirrorFile(sourceFile);
        if (mirror == null || !Files.exists(sourceFile)) {
            return;
        }
        copyFileAtomically(sourceFile, mirror);
    }

    // ---- Atomic write ----

    /**
     * Writes all lines to the target file atomically using a temp-then-move pattern.
     * Creates a temporary file in the same directory, writes all lines, then atomically
     * moves it to the target path. Falls back to a non-atomic move if the filesystem
     * does not support atomic moves.
     *
     * @param target the destination file path
     * @param lines  the lines to write (no trailing newline is appended automatically)
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Moves a file to the target path. Attempts an atomic move first, then falls back
     * to a standard move on filesystems that do not support atomic moves.
     */
    private static void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ---- User operations ----

    /**
     * Loads all users from {@code ta_users.csv}.
     * Skips the header row and blank lines. Rows with fewer than 11 fields are skipped.
     *
     * @return a list of all deserialised User objects
     */
    public List<User> loadUsers() {
        synchronized (IO_LOCK) {
            List<User> users = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(usersFile, StandardCharsets.UTF_8)) {
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
                    User user = new User();
                    user.setUserId(f.get(0));
                    user.setUsername(f.get(1));
                    user.setPassword(f.get(2));
                    user.setName(f.get(3));
                    user.setEmail(f.get(4));
                    user.setRole(UserRole.fromString(f.get(5)));
                    user.setYear(parseInt(f.get(6), 0));
                    user.setMajor(f.get(7));
                    user.setSkills(f.get(8));
                    user.setStatus(f.get(9));
                    user.setAvailability(f.get(10));
                    user.setPersonalStatement(f.size() > 11 ? f.get(11) : "");
                    user.setRelevantCourses(f.size() > 12 ? f.get(12) : "");
                    user.setProjectExperience(f.size() > 13 ? f.get(13) : "");
                    user.setPreferredRole(f.size() > 14 ? f.get(14) : "");
                    user.setSummaryStatus(f.size() > 15 ? f.get(15) : "");
                    user.setCvStoredName(f.size() > 16 ? f.get(16) : "");
                    user.setCvOriginalName(f.size() > 17 ? f.get(17) : "");
                    user.setCvContentType(f.size() > 18 ? f.get(18) : "");
                    user.setCvUploadedAt(f.size() > 19 ? f.get(19) : "");
                    user.setCvStatus(f.size() > 20 ? f.get(20) : "");
                    users.add(user);
                }
            } catch (IOException e) {
                throw new RuntimeException("failed to load users data: " + e.getMessage(), e);
            }
            return users;
        }
    }

    /**
     * Saves all users to {@code ta_users.csv}, replacing the entire file contents.
     * Null field values are written as empty strings. The write is atomic.
     *
     * @param users the complete list of users to persist
     */
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
                            user.getAvailability() == null ? "" : user.getAvailability(),
                            user.getPersonalStatement() == null ? "" : user.getPersonalStatement(),
                            user.getRelevantCourses() == null ? "" : user.getRelevantCourses(),
                            user.getProjectExperience() == null ? "" : user.getProjectExperience(),
                            user.getPreferredRole() == null ? "" : user.getPreferredRole(),
                            user.getSummaryStatus() == null ? "" : user.getSummaryStatus(),
                            user.getCvStoredName() == null ? "" : user.getCvStoredName(),
                            user.getCvOriginalName() == null ? "" : user.getCvOriginalName(),
                            user.getCvContentType() == null ? "" : user.getCvContentType(),
                            user.getCvUploadedAt() == null ? "" : user.getCvUploadedAt(),
                            user.getCvStatus() == null ? "" : user.getCvStatus()
                    ));
                }
                writeLinesAtomically(usersFile, lines);
                syncFileToMirror(usersFile);
            } catch (IOException e) {
                throw new RuntimeException("failed to save users data: " + e.getMessage(), e);
            }
        }
    }

    // ---- Job operations ----

    /**
     * Loads all jobs from {@code jobs.csv}. Skips the header row and blank lines.
     * For backward compatibility, rows with fewer than 12 fields are padded with empty
     * strings up to 12 fields.
     *
     * @return a list of all deserialised Job objects
     */
    public List<Job> loadJobs() {
        synchronized (IO_LOCK) {
            List<Job> jobs = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(jobsFile, StandardCharsets.UTF_8)) {
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

    /**
     * Saves all jobs to {@code jobs.csv}, replacing the entire file contents.
     * The write is atomic.
     *
     * @param jobs the complete list of jobs to persist
     */
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
                writeLinesAtomically(jobsFile, lines);
                syncFileToMirror(jobsFile);
            } catch (IOException e) {
                throw new RuntimeException("failed to save jobs data: " + e.getMessage(), e);
            }
        }
    }

    // ---- Application operations ----

    /**
     * Loads all applications from {@code applications.csv}.
     * Skips the header row and blank lines. Rows with fewer than 6 fields are skipped.
     *
     * @return a list of all deserialised Application objects
     */
    public List<Application> loadApplications() {
        synchronized (IO_LOCK) {
            List<Application> apps = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(applicationsFile, StandardCharsets.UTF_8)) {
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

    /**
     * Saves all applications to {@code applications.csv}, replacing the entire file
     * contents. The write is atomic.
     *
     * @param apps the complete list of applications to persist
     */
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
                writeLinesAtomically(applicationsFile, lines);
                syncFileToMirror(applicationsFile);
            } catch (IOException e) {
                throw new RuntimeException("failed to save applications data: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Removes application rows whose IDs are in the given list from the applications CSV.
     *
     * @param applicationIds the IDs of applications to delete
     */
    public void deleteApplications(List<String> applicationIds) {
        List<Application> apps = loadApplications();
        apps.removeIf(app -> applicationIds.contains(app.getApplicationId()));
        saveApplications(apps);
    }

    // ---- Utility ----

    /**
     * Returns the current timestamp formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @return formatted timestamp string
     */
    public String nowText() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    /**
     * Parses a string to an integer, returning a default value on failure.
     *
     * @param value        the string to parse
     * @param defaultValue the value to return if parsing fails
     * @return the parsed integer or the default
     */
    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // ---- CSV serialisation helpers ----

    /**
     * Serialises a variable number of string values into a CSV line,
     * quoting fields that contain commas, quotes, or newlines.
     */
    private static String toCsv(String... values) {
        List<String> escaped = new ArrayList<>();
        for (String v : values) {
            escaped.add(escapeCsv(v));
        }
        return String.join(",", escaped);
    }

    /**
     * Escapes a value for CSV output. Fields containing comma, double-quote, or
     * newline are wrapped in double quotes with internal quotes doubled.
     *
     * @param value the raw field value
     * @return the escaped string safe for CSV output
     */
    private static String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n")) {
            safe = safe.replace("\"", "\"\"");
            return "\"" + safe + "\"";
        }
        return safe;
    }

    /**
     * Parses a single CSV line into a list of fields.
     * Handles quoted fields containing commas and escaped double-quotes.
     *
     * @param line the raw CSV line
     * @return a list of individual field values
     */
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
