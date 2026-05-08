package com.example.pdfconverter.storage;

/**
 * A {@link StorageResult} where the converted bytes are returned inline.
 *
 * <p>Used by {@code PassthroughFileStorage}: no file is written to disk or any remote
 * store — the content is carried directly in the result and returned to the HTTP client.
 *
 * @param content   the raw bytes of the converted file
 * @param filename  suggested download filename
 * @param mediaType HTTP Content-Type
 */
public record InlineResult(
        byte[] content,
        String filename,
        String mediaType
) implements StorageResult {}
