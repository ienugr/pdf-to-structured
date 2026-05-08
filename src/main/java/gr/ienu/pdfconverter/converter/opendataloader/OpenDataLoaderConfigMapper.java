package gr.ienu.pdfconverter.converter.opendataloader;

import gr.ienu.pdfconverter.domain.ConversionJob;
import gr.ienu.pdfconverter.domain.ConversionOptions;
import gr.ienu.pdfconverter.domain.OutputFormat;
import org.opendataloader.pdf.api.Config;

/**
 * Maps a {@link ConversionJob} onto an OpenDataLoader {@link Config} object.
 *
 * <p>Isolating this mapping in its own class keeps the adapter lean and makes it
 * trivial to update when the OpenDataLoader API changes between versions.
 */
class OpenDataLoaderConfigMapper {

    /**
     * Builds a fully configured {@link Config} that will write output to
     * {@code outputFolder} in the format requested by {@code job}.
     */
    Config toConfig(ConversionJob job, String outputFolder) {
        Config config = new Config();
        config.setOutputFolder(outputFolder);

        applyFormat(config, job.format());
        applyOptions(config, job.options());

        return config;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void applyFormat(Config config, OutputFormat format) {
        // Disable the default JSON output so that only the requested format is written.
        config.setGenerateJSON(false);

        switch (format) {
            case JSON -> config.setGenerateJSON(true);
            case TEXT -> config.setGenerateText(true);
            case HTML -> config.setGenerateHtml(true);
            case PDF -> config.setGeneratePDF(true);
            case MARKDOWN -> config.setGenerateMarkdown(true);
            case MARKDOWN_WITH_HTML -> {
                // setUseHTMLInMarkdown automatically enables markdown generation
                config.setUseHTMLInMarkdown(true);
            }
            case MARKDOWN_WITH_IMAGES -> {
                // Embed images as Base64 so the output is a self-contained .md file.
                // setAddImageToMarkdown automatically enables markdown generation.
                config.setAddImageToMarkdown(true);
                config.setImageOutput(Config.IMAGE_OUTPUT_EMBEDDED);
            }
            case TAGGED_PDF -> config.setGenerateTaggedPDF(true);
        }
    }

    private void applyOptions(Config config, ConversionOptions options) {
        if (options == null) {
            return;
        }
        if (options.getPassword() != null) {
            config.setPassword(options.getPassword());
        }
        if (options.getPages() != null) {
            config.setPages(options.getPages());
        }
        config.setKeepLineBreaks(options.isKeepLineBreaks());
        config.setIncludeHeaderFooter(options.isIncludeHeaderFooter());

        if (options.getReadingOrder() != null && Config.isValidReadingOrder(options.getReadingOrder())) {
            config.setReadingOrder(options.getReadingOrder());
        }
        if (options.getTableMethod() != null && Config.isValidTableMethod(options.getTableMethod())) {
            config.setTableMethod(options.getTableMethod());
        }
    }
}
