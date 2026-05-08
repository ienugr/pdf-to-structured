package gr.ienu.pdfconverter.storage.passthrough;

import gr.ienu.pdfconverter.domain.ConversionResult;
import gr.ienu.pdfconverter.storage.FileStorage;
import gr.ienu.pdfconverter.storage.InlineResult;
import gr.ienu.pdfconverter.storage.StorageResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * {@link FileStorage} adapter that does <em>not</em> persist anything.
 *
 * <p>The converted bytes are returned inline to the caller, which streams them directly
 * back to the HTTP client.  This is the default storage strategy — ideal for stateless
 * deployments where no shared filesystem or object store is available.
 *
 * <p>To activate: set {@code app.storage.type=passthrough} (this is the default when the
 * property is absent).
 */
@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "passthrough", matchIfMissing = true)
public class PassthroughFileStorage implements FileStorage {

    @Override
    public StorageResult store(ConversionResult result) {
        return new InlineResult(result.content(), result.filename(), result.mediaType());
    }
}
