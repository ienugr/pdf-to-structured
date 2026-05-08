package com.example.pdfconverter.domain;

/**
 * The output of a successful PDF conversion — a byte array together with its metadata.
 *
 * @param content   converted file contents
 * @param filename  suggested filename for the converted output
 * @param mediaType HTTP Content-Type for the converted output
 */
public record ConversionResult(
        byte[] content,
        String filename,
        String mediaType
) {}
