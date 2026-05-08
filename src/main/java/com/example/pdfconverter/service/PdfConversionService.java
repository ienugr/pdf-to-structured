package com.example.pdfconverter.service;

import com.example.pdfconverter.converter.PdfConverter;
import com.example.pdfconverter.domain.ConversionJob;
import com.example.pdfconverter.domain.ConversionResult;
import com.example.pdfconverter.storage.FileStorage;
import com.example.pdfconverter.storage.StorageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service that orchestrates PDF conversion and file storage.
 *
 * <p>This class contains no business logic of its own — it simply delegates:
 * <ol>
 *   <li>to {@link PdfConverter} to perform the actual format transformation, and</li>
 *   <li>to {@link FileStorage} to decide what to do with the resulting bytes.</li>
 * </ol>
 *
 * <p>Both dependencies are injected as interfaces, so the concrete implementation
 * (OpenDataLoader, local storage, S3, …) can be swapped at any time without
 * modifying this class.
 */
@Service
public class PdfConversionService {

    private static final Logger log = LoggerFactory.getLogger(PdfConversionService.class);

    private final PdfConverter pdfConverter;
    private final FileStorage fileStorage;

    public PdfConversionService(PdfConverter pdfConverter, FileStorage fileStorage) {
        this.pdfConverter = pdfConverter;
        this.fileStorage = fileStorage;
    }

    /**
     * Converts the PDF described by {@code job} and handles the resulting file
     * according to the active {@link FileStorage} strategy.
     *
     * @param job the conversion request
     * @return a {@link StorageResult} that is either {@code InlineResult} (bytes ready to
     *         stream) or {@code UrlResult} (download URL for a persisted file)
     */
    public StorageResult convert(ConversionJob job) {
        log.info("Starting conversion: '{}' → {}", job.originalFilename(), job.format());

        ConversionResult converted = pdfConverter.convert(job);
        StorageResult stored = fileStorage.store(converted);

        log.info("Conversion complete: '{}' → {}", job.originalFilename(), job.format());
        return stored;
    }
}
