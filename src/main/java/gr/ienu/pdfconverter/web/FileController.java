package gr.ienu.pdfconverter.web;

import gr.ienu.pdfconverter.storage.local.LocalStorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serves files that were persisted by {@code LocalFileStorage}.
 *
 * <p>Only active when {@code app.storage.type=local}; omitted entirely for the
 * passthrough strategy since there are no stored files to serve.
 */
@RestController
@RequestMapping("/api/v1/files")
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local")
public class FileController {

    private final LocalStorageProperties properties;

    public FileController(LocalStorageProperties properties) {
        this.properties = properties;
    }

    /**
     * Downloads a previously converted file by its stored filename.
     *
     * @param filename the UUID-prefixed filename as returned by the conversion endpoint
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Path filePath = Paths.get(properties.getDir()).resolve(filename).normalize();

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
