package edu.bupt.ta.model;

import java.time.LocalDateTime;

public class SystemLog {
    private String logId;
    private String operatorId;
    private String operatorName;
    private String operationType;
    private String targetType;
    private String targetId;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;

    public SystemLog() {
        this.createdAt = LocalDateTime.now();
    }

    public SystemLog(String logId, String operatorId, String operatorName, String operationType,
                     String targetType, String targetId, String details, String ipAddress) {
        this.logId = logId;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operationType = operationType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.details = details;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtText() {
        return createdAt != null ? createdAt.toString().replace("T", " ") : "";
    }

    public String getOperationTypeLabel() {
        if (operationType == null) return "unknown";
        switch (operationType) {
            case "LOGIN": return "login";
            case "LOGOUT": return "logout";
            case "CREATE": return "create";
            case "UPDATE": return "update";
            case "DELETE": return "delete";
            case "APPROVE": return "approve";
            case "REJECT": return "reject";
            case "ENABLE": return "enable";
            case "DISABLE": return "disable";
            case "EXPORT": return "export";
            default: return operationType;
        }
    }
}
