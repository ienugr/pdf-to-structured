package com.example.pdfconverter.converter.opendataloader;

import com.example.pdfconverter.converter.PdfConverter;
import com.example.pdfconverter.domain.ConversionJob;
import com.example.pdfconverter.domain.ConversionResult;
import com.example.pdfconverter.domain.OutputFormat;
import com.example.pdfconverter.domain.exception.ConversionException;
import org.opendataloader.pdf.api.Config;
import org.opendataloader.pdf.api.OpenDataLoaderPDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * {@link PdfConverter} adapter backed by the OpenDataLoader PDF library.
 *
 * <p>For each conversion request the adapter:
 * <ol>
 *   <li>Writes the uploaded PDF bytes to a temporary directory.</li>
 *   <li>Configures OpenDataLoader via {@link OpenDataLoaderConfigMapper}.</li>
 *   <li>Calls {@link OpenDataLoaderPDF#processFile} to perform the conversion.</li>
 *   <li>Reads the output file back into memory.</li>
 *   <li>Cleans up the temporary directory.</li>
 * </ol>
 *
 * <p>Swapping this adapter for a different library only requires implementing
 * {@link PdfConverter} and adjusting the Spring bean wiring.
 */
@Component
public class OpenDataLoaderConverter implements PdfConverter {

    private static final Logger log = LoggerFactory.getLogger(OpenDataLoaderConverter.class);

    /** Fixed input filename inside the temp directory — keeps output file lookup deterministic. */
    private static final String INPUT_FILENAME = "input.pdf";

    private final OpenDataLoaderConfigMapper configMapper = new OpenDataLoaderConfigMapper();

    @Override
    public ConversionResult convert(ConversionJob job) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("pdf-converter-");
            log.debug("Created temp dir {} for conversion of '{}' → {}",
                    tempDir, job.originalFilename(), job.format());

            Path inputFile = tempDir.resolve(INPUT_FILENAME);
            Files.write(inputFile, job.pdfBytes());

            Config config = configMapper.toConfig(job, tempDir.toString());
            OpenDataLoaderPDF.processFile(inputFile.toString(), config);

            return readOutput(tempDir, job.format(), job.originalFilename());

        } catch (ConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConversionException(
                    "Failed to convert PDF '" + job.originalFilename() + "' to " + job.format(), e);
        } finally {
            deleteQuietly(tempDir);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Reads the converted output file from {@code tempDir}.
     *
     * <p>The output filename is derived from the fixed input name and the format's extension:
     * e.g. {@code input.md} for Markdown.
     */
    private ConversionResult readOutput(Path tempDir, OutputFormat format, String originalFilename) throws IOException {
        String outputFilename = "input" + format.getFileExtension();
        Path outputFile = tempDir.resolve(outputFilename);

        if (!Files.exists(outputFile)) {
            throw new ConversionException(
                    "Conversion produced no output file. Expected: " + outputFile);
        }

        byte[] content = Files.readAllBytes(outputFile);
        String resultFilename = buildResultFilename(originalFilename, format);

        log.debug("Conversion complete — {} bytes, filename '{}'", content.length, resultFilename);
        return new ConversionResult(content, resultFilename, format.getMediaType());
    }

    /**
     * Derives a user-friendly output filename from the original PDF name.
     * Falls back to {@code "output"} if the original filename is blank.
     */
    private String buildResultFilename(String originalFilename, OutputFormat format) {
        String baseName = "output";
        if (originalFilename != null && !originalFilename.isBlank()) {
            String name = originalFilename.trim();
            if (name.toLowerCase().endsWith(".pdf")) {
                name = name.substring(0, name.length() - 4);
            }
            baseName = name;
        }
        return baseName + format.getFileExtension();
    }

    /** Deletes a directory tree without throwing if anything goes wrong. */
    private void deleteQuietly(Path dir) {
        if (dir == null) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {
                        // best-effort cleanup
                    }
                });
        } catch (IOException e) {
            log.warn("Could not fully clean up temp directory {}: {}", dir, e.getMessage());
        }
    }
}
