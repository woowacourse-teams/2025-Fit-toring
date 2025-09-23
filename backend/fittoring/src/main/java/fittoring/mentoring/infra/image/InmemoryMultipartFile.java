package fittoring.mentoring.infra.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

final class InmemoryMultipartFile implements MultipartFile {
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] bytes;

    /**
     * Creates a new in-memory implementation of a Spring MultipartFile.
     *
     * @param name the name of the parameter in the multipart form
     * @param originalFilename the original filename as provided by the client
     * @param contentType the MIME type of the content, may be null
     * @param bytes the file content as a byte array; may be null to represent an empty file
     */
    InmemoryMultipartFile(String name, String originalFilename, String contentType, byte[] bytes) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    /**
     * Returns the name of the multipart file, i.e. the form field name with which this file was submitted.
     *
     * @return the form field name for this multipart file
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the original filename from the client's filesystem as provided when this
     * in-memory multipart file was created.
     *
     * @return the original filename, or {@code null} if none was provided
     */
    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    /**
     * Returns the MIME content type of this in-memory multipart file.
     *
     * @return the content type (e.g. "image/png"), or {@code null} if not specified
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns whether this in-memory file contains no content.
     *
     * A file is considered empty when its internal byte array is null or has zero length.
     *
     * @return true if the file contains no bytes, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return bytes == null || bytes.length == 0;
    }

    /**
     * Returns the size of the underlying content in bytes.
     *
     * @return the number of bytes of the file content
     * @throws NullPointerException if the underlying byte array is null
     */
    @Override
    public long getSize() {
        return bytes.length;
    }

    /**
     * Returns the raw content of this in-memory multipart file.
     *
     * The returned byte array is the actual backing array (not a defensive copy) and may be {@code null}
     * when the file has no content.
     *
     * @return the file content as a byte array, or {@code null} if empty
     */
    @Override
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Returns a new ByteArrayInputStream that reads from the file's in-memory bytes.
     *
     * The returned stream is independent of other streams and starts at the beginning
     * of the stored byte array.
     *
     * @return a new ByteArrayInputStream over the backing byte array
     * @throws NullPointerException if the backing byte array is null
     */
    @Override
    public ByteArrayInputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Writes the in-memory file content to the given destination File.
     *
     * The destination file is created or overwritten with the stored bytes.
     *
     * @param dest the target file to write the content to
     * @throws IOException if an I/O error occurs while writing to the destination
     */
    @Override
    public void transferTo(java.io.File dest) throws IOException {
        try (var is = getInputStream(); var os = new java.io.FileOutputStream(dest)) {
            is.transferTo(os);
        }
    }
}
