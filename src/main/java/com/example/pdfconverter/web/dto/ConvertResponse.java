package com.example.pdfconverter.web.dto;

/**
 * JSON response body returned when the converted file has been persisted
 * (i.e. the active storage strategy is NOT passthrough).
 *
 * @param url       download URL for the stored file
 * @param filename  original suggested filename
 * @param mediaType HTTP Content-Type of the stored file
 */
public record ConvertResponse(
        String url,
        String filename,
        String mediaType
) {}
