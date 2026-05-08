package com.example.pdfconverter.storage.local;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the local filesystem storage adapter.
 *
 * <p>Bound from {@code app.storage.local.*} in {@code application.yml}:
 * <pre>
 * app:
 *   storage:
 *     type: local
 *     local:
 *       dir: /var/app/uploads
 *       base-url: https://myserver.example.com
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "app.storage.local")
public class LocalStorageProperties {

    /** Absolute or relative path to the directory where converted files are written. */
    private String dir = "./uploads";

    /**
     * Base URL used to construct download links returned to clients.
     * Should not have a trailing slash.
     */
    private String baseUrl = "http://localhost:8080";

    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
}
