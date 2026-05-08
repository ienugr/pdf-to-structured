package com.example.pdfconverter.storage.local;

import com.example.pdfconverter.domain.ConversionResult;
import com.example.pdfconverter.domain.exception.StorageException;
import com.example.pdfconverter.storage.FileStorage;
import com.example.pdfconverter.storage.StorageResult;
import com.example.pdfconverter.storage.UrlResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * {@link FileStorage} adapter that persists converted files on the local filesystem.
 *
 * <p>Each stored file is assigned a UUID-prefixed name to avoid collisions.
 * A download URL is constructed from {@link LocalStorageProperties#getBaseUrl()} and
 * served by {@code FileController} via {@code GET /api/v1/files/{storedFilename}}.
 *
 * <p>To activate: set {@code app.storage.type=local} in {@code application.yml}.
 *
 * <p>To extend to S3, R2, or another store, implement {@link FileStorage} and
 * activate it via a matching {@code @ConditionalOnProperty}.
 */
@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local")
public class LocalFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorage.class);

    private final LocalStorageProperties properties;

    public LocalFileStorage(LocalStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public StorageResult store(ConversionResult result) {
        Path storageDir = Paths.get(properties.getDir());
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            throw new StorageException("Cannot create storage directory: " + storageDir, e);
        }

        // Prefix with UUID to guarantee uniqueness; preserve the original filename for readability.
        String storedFilename = UUID.randomUUID() + "_" + result.filename();
        Path targetPath = storageDir.resolve(storedFilename);

        try {
            Files.write(targetPath, result.content());
        } catch (IOException e) {
            throw new StorageException("Failed to write file to " + targetPath, e);
        }

        String downloadUrl = properties.getBaseUrl() + "/api/v1/files/" + storedFilename;
        log.info("Stored converted file '{}' → {}", result.filename(), downloadUrl);

        return new UrlResult(downloadUrl, result.filename(), result.mediaType());
    }
}
