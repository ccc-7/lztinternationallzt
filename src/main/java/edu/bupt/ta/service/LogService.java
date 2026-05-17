package edu.bupt.ta.service;

import edu.bupt.ta.model.SystemLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the system audit log stored in {@code data/system_logs.csv}.
 * Provides methods to append log entries and query them by operator, type,
 * date range, keyword, and pagination. All public methods are synchronized.
 */
public class LogService {

    /** CSV column header for system_logs.csv (9 columns). */
    private static final String LOGS_HEADER =
            "logId,operatorId,operatorName,operationType,targetType,targetId,details,ipAddress,createdAt";

    private final Path logsFile;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Default constructor. Uses the default logs file path relative to the working directory.
     */
    public LogService() {
        this(Paths.get("data", "system_logs.csv"));
    }

    /**
     * Constructor with injected logs file path. Used by tests.
     *
     * @param logsFile the absolute path to the system_logs.csv file
     */
    public LogService(Path logsFile) {
        this.logsFile = logsFile;
        initFile();
    }

    private void initFile() {
        try {
            Files.createDirectories(logsFile.getParent());
            if (!Files.exists(logsFile)) {
                try (BufferedWriter writer = Files.newBufferedWriter(logsFile, StandardCharsets.UTF_8)) {
                    writer.write(LOGS_HEADER);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to initialize file：" + e.getMessage(), e);
        }
    }

    /**
     * Appends a new audit log entry to the system log file.
     * Assigns the next sequential log ID and sets the creation timestamp to now.
     *
     * @param operatorId    userId of the actor performing the action
     * @param operatorName  display name of the actor
     * @param operationType action type, e.g. LOGIN, CREATE, UPDATE, DELETE, APPROVE, REJECT
     * @param targetType   entity type acted upon, e.g. User, Job, Application
     * @param targetId     ID of the entity acted upon
     * @param details      human-readable description of the operation
     * @param ipAddress    client IP address
     */
    public synchronized void log(String operatorId, String operatorName, String operationType,
                                 String targetType, String targetId, String details, String ipAddress) {
        List<SystemLog> logs = loadLogs();
        SystemLog log = new SystemLog();
        log.setLogId(nextLogId(logs));
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationType(operationType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setCreatedAt(LocalDateTime.now());

        logs.add(log);
        saveLogs(logs);
    }

    /**
     * @return all log entries sorted newest-first
     */
    public synchronized List<SystemLog> getAllLogs() {
        List<SystemLog> logs = loadLogs();
        logs.sort(Comparator.comparing(SystemLog::getCreatedAt).reversed());
        return logs;
    }

    /**
     * @param operatorId the operator ID to filter by
     * @return all log entries for this operator, sorted newest-first
     */
    public synchronized List<SystemLog> getLogsByOperator(String operatorId) {
        return loadLogs().stream()
                .filter(log -> log.getOperatorId().equals(operatorId))
                .sorted(Comparator.comparing(SystemLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * @param operationType the operation type to filter by
     * @return all log entries with this type, sorted newest-first
     */
    public synchronized List<SystemLog> getLogsByType(String operationType) {
        return loadLogs().stream()
                .filter(log -> log.getOperationType().equals(operationType))
                .sorted(Comparator.comparing(SystemLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * @param start the start of the date range (inclusive)
     * @param end   the end of the date range (inclusive)
     * @return all log entries in the range, sorted newest-first
     */
    public synchronized List<SystemLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return loadLogs().stream()
                .filter(log -> !log.getCreatedAt().isBefore(start) && !log.getCreatedAt().isAfter(end))
                .sorted(Comparator.comparing(SystemLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * @param keyword the keyword to search for (case-insensitive)
     * @return matching log entries, sorted newest-first
     */
    public synchronized List<SystemLog> searchLogs(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllLogs();
        }
        String lowerKeyword = keyword.toLowerCase();
        return loadLogs().stream()
                .filter(log -> (log.getOperatorName() != null && log.getOperatorName().toLowerCase().contains(lowerKeyword))
                        || (log.getDetails() != null && log.getDetails().toLowerCase().contains(lowerKeyword))
                        || (log.getTargetId() != null && log.getTargetId().toLowerCase().contains(lowerKeyword)))
                .sorted(Comparator.comparing(SystemLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Returns a single page of log entries, sorted newest first.
     *
     * @param page     1-based page number
     * @param pageSize number of entries per page
     * @return a list of at most pageSize entries, or an empty list if page is out of range
     */
    public synchronized List<SystemLog> getLogsPaginated(int page, int pageSize) {
        List<SystemLog> allLogs = getAllLogs();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allLogs.size());
        if (start >= allLogs.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(allLogs.subList(start, end));
    }

    /**
     * Calculates the total number of pages for a given page size.
     *
     * @param pageSize entries per page
     * @return total page count (minimum 0)
     */
    public synchronized int getTotalPages(int pageSize) {
        int total = loadLogs().size();
        return (int) Math.ceil((double) total / pageSize);
    }

    /** Reads all log entries from the CSV file. Returns an empty list if the file does not exist. */
    private List<SystemLog> loadLogs() {
        List<SystemLog> logs = new ArrayList<>();
        if (!Files.exists(logsFile)) {
            return logs;
        }
            try (BufferedReader reader = Files.newBufferedReader(logsFile, StandardCharsets.UTF_8)) {
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
                if (f.size() < 9) {
                    continue;
                }
                SystemLog log = new SystemLog();
                log.setLogId(f.get(0));
                log.setOperatorId(f.get(1));
                log.setOperatorName(f.get(2));
                log.setOperationType(f.get(3));
                log.setTargetType(f.get(4));
                log.setTargetId(f.get(5));
                log.setDetails(f.get(6));
                log.setIpAddress(f.get(7));
                try {
                    log.setCreatedAt(LocalDateTime.parse(f.get(8), FORMATTER));
                } catch (Exception e) {
                    log.setCreatedAt(LocalDateTime.now());
                }
                logs.add(log);
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to read log data：" + e.getMessage(), e);
        }
        return logs;
    }

    /** Overwrites the entire log file with the given list of entries. */
    private synchronized void saveLogs(List<SystemLog> logs) {
        try (BufferedWriter writer = Files.newBufferedWriter(logsFile, StandardCharsets.UTF_8)) {
            writer.write(LOGS_HEADER);
            writer.newLine();
            for (SystemLog log : logs) {
                writer.write(toCsv(
                        log.getLogId(),
                        log.getOperatorId(),
                        log.getOperatorName(),
                        log.getOperationType(),
                        log.getTargetType(),
                        log.getTargetId(),
                        log.getDetails(),
                        log.getIpAddress(),
                        log.getCreatedAtText()
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to save log data：" + e.getMessage(), e);
        }
    }

    /** Generates the next sequential log ID (e.g. "L00042"). */
    private String nextLogId(List<SystemLog> logs) {
        int max = logs.stream()
                .map(SystemLog::getLogId)
                .filter(id -> id != null && id.startsWith("L"))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(1));
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Comparator.naturalOrder())
                .orElse(0);
        return String.format("L%05d", max + 1);
    }

    /** Serialises multiple fields into a CSV line using escapeCsv. */
    private String toCsv(String... values) {
        List<String> escaped = new ArrayList<>();
        for (String v : values) {
            escaped.add(escapeCsv(v));
        }
        return String.join(",", escaped);
    }

    /** Escapes a value for CSV output. */
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
