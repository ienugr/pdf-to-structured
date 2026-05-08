package com.example.pdfconverter.web;

import com.example.pdfconverter.domain.exception.ConversionException;
import com.example.pdfconverter.domain.exception.StorageException;
import com.example.pdfconverter.domain.exception.UnsupportedFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;

/**
 * Centralised exception handling for the REST layer.
 *
 * <p>Maps application-level exceptions to RFC 7807 {@link ProblemDetail} responses
 * so that clients receive structured, machine-readable error information.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UnsupportedFormatException.class)
    public ProblemDetail handleUnsupportedFormat(UnsupportedFormatException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setType(URI.create("https://pdf-converter/errors/unsupported-format"));
        detail.setTitle("Unsupported Output Format");
        detail.setProperty("requestedFormat", ex.getRequestedFormat());
        return detail;
    }

    @ExceptionHandler(ConversionException.class)
    public ProblemDetail handleConversionFailure(ConversionException ex) {
        log.error("PDF conversion failed", ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setType(URI.create("https://pdf-converter/errors/conversion-failed"));
        detail.setTitle("PDF Conversion Failed");
        return detail;
    }

    @ExceptionHandler(StorageException.class)
    public ProblemDetail handleStorageFailure(StorageException ex) {
        log.error("File storage operation failed", ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Storage operation failed");
        detail.setType(URI.create("https://pdf-converter/errors/storage-failed"));
        detail.setTitle("Storage Failure");
        return detail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleFileTooLarge(MaxUploadSizeExceededException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Uploaded file exceeds the maximum allowed size.");
        detail.setType(URI.create("https://pdf-converter/errors/file-too-large"));
        detail.setTitle("File Too Large");
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setType(URI.create("https://pdf-converter/errors/invalid-request"));
        detail.setTitle("Invalid Request");
        return detail;
    }
}
