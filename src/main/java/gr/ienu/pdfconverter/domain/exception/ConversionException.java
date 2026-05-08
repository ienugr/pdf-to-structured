package gr.ienu.pdfconverter.domain.exception;

/**
 * Thrown when the underlying PDF converter fails to process a document.
 */
public class ConversionException extends RuntimeException {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
