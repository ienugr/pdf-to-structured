package gr.ienu.pdfconverter.domain;

/**
 * Optional parameters that influence how a PDF is converted.
 *
 * <p>All fields are optional; use {@link #defaults()} when no special behaviour is required.
 * The builder is exposed so callers can set only the fields they care about:
 * <pre>{@code
 * ConversionOptions opts = ConversionOptions.builder()
 *     .password("secret")
 *     .pages("1,3,5-7")
 *     .keepLineBreaks(true)
 *     .build();
 * }</pre>
 */
public final class ConversionOptions {

    /** Password for encrypted PDF files. {@code null} means no password. */
    private final String password;

    /**
     * Pages to extract, e.g. {@code "1,3,5-7"}.
     * {@code null} means all pages.
     */
    private final String pages;

    /** Preserve original line breaks instead of merging lines into paragraphs. */
    private final boolean keepLineBreaks;

    /** Include page headers and footers in the output. */
    private final boolean includeHeaderFooter;

    /**
     * Reading order algorithm.
     * Supported values: {@code "xycut"} (default), {@code "off"}.
     */
    private final String readingOrder;

    /**
     * Table detection method.
     * Supported values: {@code "default"} (border-based), {@code "cluster"}.
     */
    private final String tableMethod;

    private ConversionOptions(Builder builder) {
        this.password = builder.password;
        this.pages = builder.pages;
        this.keepLineBreaks = builder.keepLineBreaks;
        this.includeHeaderFooter = builder.includeHeaderFooter;
        this.readingOrder = builder.readingOrder;
        this.tableMethod = builder.tableMethod;
    }

    public String getPassword() { return password; }
    public String getPages() { return pages; }
    public boolean isKeepLineBreaks() { return keepLineBreaks; }
    public boolean isIncludeHeaderFooter() { return includeHeaderFooter; }
    public String getReadingOrder() { return readingOrder; }
    public String getTableMethod() { return tableMethod; }

    /** Returns a {@link ConversionOptions} with all defaults applied. */
    public static ConversionOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String password = null;
        private String pages = null;
        private boolean keepLineBreaks = false;
        private boolean includeHeaderFooter = false;
        private String readingOrder = "xycut";
        private String tableMethod = "default";

        private Builder() {}

        public Builder password(String password) { this.password = password; return this; }
        public Builder pages(String pages) { this.pages = pages; return this; }
        public Builder keepLineBreaks(boolean keepLineBreaks) { this.keepLineBreaks = keepLineBreaks; return this; }
        public Builder includeHeaderFooter(boolean includeHeaderFooter) { this.includeHeaderFooter = includeHeaderFooter; return this; }
        public Builder readingOrder(String readingOrder) { this.readingOrder = readingOrder; return this; }
        public Builder tableMethod(String tableMethod) { this.tableMethod = tableMethod; return this; }

        public ConversionOptions build() {
            return new ConversionOptions(this);
        }
    }
}
