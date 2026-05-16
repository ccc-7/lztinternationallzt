package edu.bupt.ta.service;

import edu.bupt.ta.storage.FileStorageUtil;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CvFileService {

    private static final long MAX_CV_SIZE_BYTES = 5L * 1024 * 1024;
    private static final String CV_DIR = "cvs";

    private final FileStorageUtil storage = FileStorageUtil.getInstance();

    public SavedCvFile savePdf(String userId, Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() <= 0) {
            throw new IllegalArgumentException("Please choose a PDF file to upload.");
        }
        if (filePart.getSize() > MAX_CV_SIZE_BYTES) {
            throw new IllegalArgumentException("CV file size must be 5MB or smaller.");
        }

        String originalName = extractSubmittedFileName(filePart);
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("Uploaded file name is missing.");
        }
        if (!originalName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are supported for CV upload.");
        }

        String contentType = filePart.getContentType();
        if (contentType != null && !contentType.isBlank()
                && !"application/pdf".equalsIgnoreCase(contentType)
                && !"application/x-pdf".equalsIgnoreCase(contentType)) {
            throw new IllegalArgumentException("The uploaded file is not recognized as a PDF.");
        }

        Path cvDir = getCvDir();
        Files.createDirectories(cvDir);

        String storedName = userId + ".pdf";
        Path target = cvDir.resolve(storedName);
        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return new SavedCvFile(storedName, originalName, "application/pdf", filePart.getSize(), target);
    }

    public boolean deleteCv(String storedName) throws IOException {
        if (storedName == null || storedName.isBlank()) {
            return false;
        }
        Path target = getCvDir().resolve(storedName);
        return Files.deleteIfExists(target);
    }

    public Path resolveExistingCvPath(String storedName) {
        if (storedName == null || storedName.isBlank()) {
            return null;
        }
        Path target = getCvDir().resolve(storedName);
        if (Files.exists(target) && Files.isRegularFile(target)) {
            return target;
        }
        return null;
    }

    public String formatFileSize(long sizeBytes) {
        double sizeKb = sizeBytes / 1024.0;
        if (sizeKb < 1024) {
            return String.format("%.1f KB", sizeKb);
        }
        return String.format("%.2f MB", sizeKb / 1024.0);
    }

    public long getMaxCvSizeBytes() {
        return MAX_CV_SIZE_BYTES;
    }

    private Path getCvDir() {
        return storage.getBaseDir().resolve(CV_DIR);
    }

    private String extractSubmittedFileName(Part part) {
        String submitted = part.getSubmittedFileName();
        if (submitted == null) {
            return null;
        }
        int slash = Math.max(submitted.lastIndexOf('/'), submitted.lastIndexOf('\\'));
        return slash >= 0 ? submitted.substring(slash + 1) : submitted;
    }

    public static class SavedCvFile {
        private final String storedName;
        private final String originalName;
        private final String contentType;
        private final long sizeBytes;
        private final Path path;

        public SavedCvFile(String storedName, String originalName, String contentType, long sizeBytes, Path path) {
            this.storedName = storedName;
            this.originalName = originalName;
            this.contentType = contentType;
            this.sizeBytes = sizeBytes;
            this.path = path;
        }

        public String getStoredName() {
            return storedName;
        }

        public String getOriginalName() {
            return originalName;
        }

        public String getContentType() {
            return contentType;
        }

        public long getSizeBytes() {
            return sizeBytes;
        }

        public Path getPath() {
            return path;
        }
    }
}
