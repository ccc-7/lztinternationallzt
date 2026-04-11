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

public class LogService {

    private static final String LOGS_HEADER =
            "logId,operatorId,operatorName,operationType,targetType,targetId,details,ipAddress,createdAt";
    private static final Path LOGS_FILE = Paths.get("data", "system_logs.csv");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogService() {
        initFile();
    }

    private void initFile() {
        try {
            Files.createDirectories(LOGS_FILE.getParent());
            if (!Files.exists(LOGS_FILE)) {
                try (BufferedWriter writer = Files.newBufferedWriter(LOGS_FILE, StandardCharsets.UTF_8)) {
                    writer.write(LOGS_HEADER);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to initialize file：" + e.getMessage(), e);
        }
    }

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

    public synchronized List<SystemLog> getAllLogs() {
        List<SystemLog> logs = loadLogs();
        logs.sort(Comparator.comparing(SystemLog::getCreatedAt).reversed());
        return logs;
    }

    public synchronized List<SystemLog> getLogsByOperator(String operatorId) {
        return loadLogs().stream()
                .filter(log -> log.getOperatorId().equals(operatorId))
                .sorted(Comparator.comparing(SystemLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public synchronized List<SystemLog> getLogsByType(String operationType) {
        return loadLogs().stream()
                .filter(log -> log.getOperationType().equals(operationType))
                .sorted(Comparator.comparing(SystemLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public synchronized List<SystemLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return loadLogs().stream()
                .filter(log -> !log.getCreatedAt().isBefore(start) && !log.getCreatedAt().isAfter(end))
                .sorted(Comparator.comparing(SystemLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

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

    public synchronized List<SystemLog> getLogsPaginated(int page, int pageSize) {
        List<SystemLog> allLogs = getAllLogs();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allLogs.size());
        if (start >= allLogs.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(allLogs.subList(start, end));
    }

    public synchronized int getTotalPages(int pageSize) {
        int total = loadLogs().size();
        return (int) Math.ceil((double) total / pageSize);
    }

    private List<SystemLog> loadLogs() {
        List<SystemLog> logs = new ArrayList<>();
        if (!Files.exists(LOGS_FILE)) {
            return logs;
        }
        try (BufferedReader reader = Files.newBufferedReader(LOGS_FILE, StandardCharsets.UTF_8)) {
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

    private synchronized void saveLogs(List<SystemLog> logs) {
        try (BufferedWriter writer = Files.newBufferedWriter(LOGS_FILE, StandardCharsets.UTF_8)) {
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
