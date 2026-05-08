package com.example.pdfconverter.web;

import com.example.pdfconverter.domain.ConversionJob;
import com.example.pdfconverter.domain.ConversionOptions;
import com.example.pdfconverter.domain.OutputFormat;
import com.example.pdfconverter.service.PdfConversionService;
import com.example.pdfconverter.storage.InlineResult;
import com.example.pdfconverter.storage.StorageResult;
import com.example.pdfconverter.storage.UrlResult;
import com.example.pdfconverter.web.dto.ConvertResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST endpoint for PDF conversion.
 *
 * <h2>Endpoint</h2>
 * <pre>POST /api/v1/convert</pre>
 * Accepts a {@code multipart/form-data} request with:
 * <ul>
 *   <li>{@code file} — the PDF to convert (required)</li>
 *   <li>{@code format} — desired output format (required);
 *       one of: {@code json}, {@code text}, {@code html}, {@code pdf}, {@code markdown},
 *       {@code markdown-with-html}, {@code markdown-with-images}, {@code tagged-pdf}</li>
 *   <li>{@code password} — password for encrypted PDFs (optional)</li>
 *   <li>{@code pages} — page range, e.g. {@code "1,3,5-7"} (optional, default: all pages)</li>
 *   <li>{@code keepLineBreaks} — preserve original line breaks (optional, default: false)</li>
 *   <li>{@code includeHeaderFooter} — include headers/footers (optional, default: false)</li>
 *   <li>{@code readingOrder} — {@code "xycut"} or {@code "off"} (optional, default: xycut)</li>
 *   <li>{@code tableMethod} — {@code "default"} or {@code "cluster"} (optional)</li>
 * </ul>
 *
 * <h2>Response</h2>
 * <ul>
 *   <li>When using the <em>passthrough</em> storage (default): returns the converted file
 *       as a binary download.</li>
 *   <li>When using a persistent storage (local, S3, …): returns a JSON body with a
 *       {@code url} field pointing to the stored file.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1")
public class ConversionController {

    private final PdfConversionService conversionService;

    public ConversionController(PdfConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> convert(
            @RequestPart("file") MultipartFile file,
            @RequestParam("format") String format,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "pages", required = false) String pages,
            @RequestParam(value = "keepLineBreaks", defaultValue = "false") boolean keepLineBreaks,
            @RequestParam(value = "includeHeaderFooter", defaultValue = "false") boolean includeHeaderFooter,
            @RequestParam(value = "readingOrder", defaultValue = "xycut") String readingOrder,
            @RequestParam(value = "tableMethod", defaultValue = "default") String tableMethod
    ) throws IOException {

        OutputFormat outputFormat = OutputFormat.fromValue(format);

        ConversionOptions options = ConversionOptions.builder()
                .password(password)
                .pages(pages)
                .keepLineBreaks(keepLineBreaks)
                .includeHeaderFooter(includeHeaderFooter)
                .readingOrder(readingOrder)
                .tableMethod(tableMethod)
                .build();

        ConversionJob job = new ConversionJob(
                file.getBytes(),
                file.getOriginalFilename(),
                outputFormat,
                options
        );

        StorageResult result = conversionService.convert(job);

        return switch (result) {
            case InlineResult inline -> ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(inline.filename())
                                    .build()
                                    .toString())
                    .contentType(MediaType.parseMediaType(inline.mediaType()))
                    .body(inline.content());

            case UrlResult url -> ResponseEntity.ok()
                    .body(new ConvertResponse(url.url(), url.filename(), url.mediaType()));
        };
    }
}
