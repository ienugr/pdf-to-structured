package com.example.pdfconverter.domain;

/**
 * Immutable value object describing a single PDF conversion request.
 *
 * @param pdfBytes         raw bytes of the uploaded PDF
 * @param originalFilename original filename as reported by the client (may be {@code null})
 * @param format           desired output format
 * @param options          additional conversion options
 */
public record ConversionJob(
        byte[] pdfBytes,
        String originalFilename,
        OutputFormat format,
        ConversionOptions options
) {
    public ConversionJob {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new IllegalArgumentException("pdfBytes must not be null or empty");
        }
        if (format == null) {
            throw new IllegalArgumentException("format must not be null");
        }
        if (options == null) {
            options = ConversionOptions.defaults();
        }
    }
}
