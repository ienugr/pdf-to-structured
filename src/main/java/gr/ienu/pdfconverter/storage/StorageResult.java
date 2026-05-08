package gr.ienu.pdfconverter.storage;

/**
 * Sealed interface representing the outcome of a {@link FileStorage#store} call.
 *
 * <p>Exactly two subtypes exist:
 * <ul>
 *   <li>{@link InlineResult} — the content was <em>not</em> persisted; bytes are returned directly.</li>
 *   <li>{@link UrlResult} — the content was persisted and can be retrieved via a URL.</li>
 * </ul>
 *
 * <p>Using a sealed interface lets callers handle both variants exhaustively with a
 * {@code switch} expression, with no risk of an unhandled case:
 * <pre>{@code
 * return switch (storageResult) {
 *     case InlineResult inline -> ResponseEntity.ok().body(inline.content());
 *     case UrlResult url      -> ResponseEntity.ok().body(Map.of("url", url.url()));
 * };
 * }</pre>
 */
public sealed interface StorageResult permits InlineResult, UrlResult {

    /** Suggested filename for the converted output. */
    String filename();

    /** HTTP {@code Content-Type} for the converted output. */
    String mediaType();
}
