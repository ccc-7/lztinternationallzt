package edu.bupt.ta.service;

import edu.bupt.ta.storage.FileStorageUtil;
import jakarta.servlet.http.Part;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Handles physical file operations for TA CV PDFs.
 * Provides upload validation, storage to the {@code data/cvs/} directory,
 * and deletion. Does not update CSV records; callers must update
 * {@link edu.bupt.ta.model.User} CSV metadata separately.
 */
public class CvFileService {

    private static final long MAX_CV_SIZE_BYTES = 5L * 1024 * 1024;
    private static final String CV_DIR = "cvs";

    private final FileStorageUtil storage = FileStorageUtil.getInstance();

    /**
     * Saves an uploaded PDF file to the {@code data/cvs/} directory.
     * The file is stored as "{userId}.pdf" (e.g., "U001.pdf"), regardless of
     * the original filename. Validates size (&le;5 MB), file extension (.pdf),
     * and Content-Type.
     *
     * @param userId   the user ID, used as the stored filename
     * @param filePart the uploaded file part from the HTTP request
     * @return a {@link SavedCvFile} containing metadata about the saved file
     * @throws IllegalArgumentException if validation fails (size, extension, or Content-Type)
     * @throws IOException             if the file cannot be written to disk
     */
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
        byte[] content;
        try (InputStream inputStream = filePart.getInputStream()) {
            byte[] header = inputStream.readNBytes(5);
            if (header.length < 5) {
                throw new IllegalArgumentException("Invalid PDF file content.");
            }

            String headerText = new String(header, StandardCharsets.US_ASCII);
            if (!"%PDF-".equals(headerText)) {
                throw new IllegalArgumentException("Invalid PDF file content.");
            }

            byte[] remaining = inputStream.readAllBytes();
            content = new byte[header.length + remaining.length];
            System.arraycopy(header, 0, content, 0, header.length);
            System.arraycopy(remaining, 0, content, header.length, remaining.length);
        }

        try (InputStream rewritten = new ByteArrayInputStream(content)) {
            Files.copy(rewritten, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return new SavedCvFile(storedName, originalName, "application/pdf", content.length, target);
    }

    /**
     * Deletes the CV file from disk. No-op if storedName is null or blank.
     *
     * @param storedName the filename on disk (e.g. "U001.pdf")
     * @return true if the file was deleted, false if it did not exist
     * @throws IOException if the deletion fails
     */
    public boolean deleteCv(String storedName) throws IOException {
        if (storedName == null || storedName.isBlank()) {
            return false;
        }
        Path target = getCvDir().resolve(storedName);
        return Files.deleteIfExists(target);
    }

    /**
     * @param storedName the stored filename (e.g. "U001.pdf")
     * @return the absolute path to the file, or null if not found or storedName is blank
     */
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

    /**
     * @param sizeBytes the file size in bytes
     * @return a human-readable size string (e.g. "1.5 MB")
     */
    public String formatFileSize(long sizeBytes) {
        double sizeKb = sizeBytes / 1024.0;
        if (sizeKb < 1024) {
            return String.format("%.1f KB", sizeKb);
        }
        return String.format("%.2f MB", sizeKb / 1024.0);
    }

    /** @return the maximum allowed CV file size in bytes (5 MB) */
    public long getMaxCvSizeBytes() {
        return MAX_CV_SIZE_BYTES;
    }

    /** Returns the absolute path to the CV directory. */
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

    /**
     * Holds metadata about a successfully saved CV file.
     */
    public static class SavedCvFile {
        private final String storedName;
        private final String originalName;
        private final String contentType;
        private final long sizeBytes;
        private final Path path;

        /**
         * Constructs a SavedCvFile with all metadata fields.
         * @param storedName   the filename stored on disk (e.g. "U001.pdf")
         * @param originalName the original submitted filename
         * @param contentType the MIME type
         * @param sizeBytes  the file size in bytes
         * @param path       the absolute path to the file on disk
         */
        public SavedCvFile(String storedName, String originalName, String contentType, long sizeBytes, Path path) {
            this.storedName = storedName;
            this.originalName = originalName;
            this.contentType = contentType;
            this.sizeBytes = sizeBytes;
            this.path = path;
        }

        /** @return the stored filename */
        public String getStoredName() {
            return storedName;
        }

        /** @return the original submitted filename */
        public String getOriginalName() {
            return originalName;
        }

        /** @return the MIME content type */
        public String getContentType() {
            return contentType;
        }

        /** @return the file size in bytes */
        public long getSizeBytes() {
            return sizeBytes;
        }

        /** @return the absolute path to the file on disk */
        public Path getPath() {
            return path;
        }
    }
}
