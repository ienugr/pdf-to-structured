package com.example.pdfconverter.converter.opendataloader;

import jakarta.annotation.PreDestroy;
import org.opendataloader.pdf.api.OpenDataLoaderPDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Manages the OpenDataLoader JVM-level lifecycle.
 *
 * <p>The OpenDataLoader library initialises internal thread pools on first use and
 * requires a single {@link OpenDataLoaderPDF#shutdown()} call at application exit — not
 * between individual conversions.  This component hooks into the Spring context to call
 * shutdown at the right moment.
 */
@Component
public class OpenDataLoaderLifecycle {

    private static final Logger log = LoggerFactory.getLogger(OpenDataLoaderLifecycle.class);

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down OpenDataLoader internal thread pools…");
        OpenDataLoaderPDF.shutdown();
    }
}
