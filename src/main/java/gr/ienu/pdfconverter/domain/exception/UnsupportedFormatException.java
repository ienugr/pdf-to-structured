package gr.ienu.pdfconverter.domain.exception;

/**
 * Thrown when a requested output format is not supported.
 */
public class UnsupportedFormatException extends RuntimeException {

    private final String requestedFormat;

    public UnsupportedFormatException(String requestedFormat) {
        super("Unsupported output format: '" + requestedFormat + "'. "
                + "Supported values: json, text, html, pdf, markdown, "
                + "markdown-with-html, markdown-with-images, tagged-pdf");
        this.requestedFormat = requestedFormat;
    }

    public String getRequestedFormat() {
        return requestedFormat;
    }
}
