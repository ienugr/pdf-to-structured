package com.example.pdfconverter.storage;

import com.example.pdfconverter.domain.ConversionResult;

/**
 * Port (interface) for persisting or forwarding converted files.
 *
 * <p>Implementations decide <em>what to do</em> with the converted bytes:
 * <ul>
 *   <li>{@code PassthroughFileStorage} — returns the bytes directly to the caller
 *       (no persistence, ideal for stateless deployments).</li>
 *   <li>{@code LocalFileStorage} — writes to the local filesystem and returns a URL.</li>
 *   <li>Future adapters could target Amazon S3, Cloudflare R2, Google Cloud Storage, etc.</li>
 * </ul>
 *
 * <p>Adding a new storage back-end requires only a new implementation of this interface;
 * the rest of the application does not need to change.
 */
public interface FileStorage {

    /**
     * Stores (or passes through) the converted file.
     *
     * @param result the output of a successful PDF conversion
     * @return a {@link StorageResult} describing where/how to retrieve the file
     * @throws com.example.pdfconverter.domain.exception.StorageException on I/O failure
     */
    StorageResult store(ConversionResult result);
}
