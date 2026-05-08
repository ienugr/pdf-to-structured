package com.example.pdfconverter.converter;

import com.example.pdfconverter.domain.ConversionJob;
import com.example.pdfconverter.domain.ConversionResult;

/**
 * Port (interface) for PDF conversion.
 *
 * <p>Any class that can turn a PDF into a desired format should implement this interface.
 * The current default adapter is {@code OpenDataLoaderConverter}, but swapping in a
 * different library (Apache PDFBox, iText, etc.) only requires a new implementation and
 * wiring — no changes to callers.
 *
 * <p>Implementations must be thread-safe when the application is deployed with multiple
 * worker threads.
 */
public interface PdfConverter {

    /**
     * Converts the PDF described by {@code job} and returns the converted content.
     *
     * @param job describes the PDF bytes, desired format, and conversion options
     * @return the conversion output (bytes + metadata)
     * @throws com.example.pdfconverter.domain.exception.ConversionException on any conversion failure
     */
    ConversionResult convert(ConversionJob job);
}
