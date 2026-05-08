package com.example.pdfconverter.storage;

/**
 * A {@link StorageResult} where the converted file was persisted and can be fetched via a URL.
 *
 * <p>Used by storage adapters that write to a durable store (local filesystem, S3, R2, …).
 * The caller typically returns a JSON response pointing the client to {@link #url()}.
 *
 * @param url       location where the file can be downloaded
 * @param filename  suggested download filename
 * @param mediaType HTTP Content-Type
 */
public record UrlResult(
        String url,
        String filename,
        String mediaType
) implements StorageResult {}
