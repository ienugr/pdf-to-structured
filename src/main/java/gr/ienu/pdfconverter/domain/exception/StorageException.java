package gr.ienu.pdfconverter.domain.exception;

/**
 * Thrown when a file storage operation fails (e.g. disk full, permission denied).
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
