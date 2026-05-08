package gr.ienu.pdfconverter.domain;

import gr.ienu.pdfconverter.domain.exception.UnsupportedFormatException;

/**
 * All output formats supported by the conversion API.
 *
 * <p>Each constant captures the CLI/API identifier, the HTTP media type for the response,
 * and the file extension used by OpenDataLoader when writing output files.
 */
public enum OutputFormat {

    JSON("json", "application/json", ".json"),
    TEXT("text", "text/plain", ".txt"),
    HTML("html", "text/html", ".html"),
    PDF("pdf", "application/pdf", ".pdf"),
    MARKDOWN("markdown", "text/markdown", ".md"),
    MARKDOWN_WITH_HTML("markdown-with-html", "text/markdown", ".md"),
    MARKDOWN_WITH_IMAGES("markdown-with-images", "text/markdown", ".md"),
    TAGGED_PDF("tagged-pdf", "application/pdf", ".pdf");

    private final String value;
    private final String mediaType;
    private final String fileExtension;

    OutputFormat(String value, String mediaType, String fileExtension) {
        this.value = value;
        this.mediaType = mediaType;
        this.fileExtension = fileExtension;
    }

    public String getValue() {
        return value;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Looks up a format by its string identifier (case-insensitive).
     *
     * @param value the format string, e.g. {@code "markdown-with-html"}
     * @return the matching {@link OutputFormat}
     * @throws UnsupportedFormatException if no format matches
     */
    public static OutputFormat fromValue(String value) {
        for (OutputFormat format : values()) {
            if (format.value.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new UnsupportedFormatException(value);
    }
}
