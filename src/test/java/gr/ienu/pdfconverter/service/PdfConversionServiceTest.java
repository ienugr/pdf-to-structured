package gr.ienu.pdfconverter.service;

import gr.ienu.pdfconverter.converter.PdfConverter;
import gr.ienu.pdfconverter.domain.ConversionJob;
import gr.ienu.pdfconverter.domain.ConversionOptions;
import gr.ienu.pdfconverter.domain.ConversionResult;
import gr.ienu.pdfconverter.domain.OutputFormat;
import gr.ienu.pdfconverter.storage.FileStorage;
import gr.ienu.pdfconverter.storage.InlineResult;
import gr.ienu.pdfconverter.storage.StorageResult;
import gr.ienu.pdfconverter.storage.UrlResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfConversionServiceTest {

    @Mock
    private PdfConverter pdfConverter;

    @Mock
    private FileStorage fileStorage;

    private PdfConversionService service;

    @BeforeEach
    void setUp() {
        service = new PdfConversionService(pdfConverter, fileStorage);
    }

    @Test
    void convert_delegatesToConverterAndStorage() {
        byte[] pdfBytes = "fake-pdf".getBytes();
        ConversionJob job = new ConversionJob(pdfBytes, "report.pdf", OutputFormat.MARKDOWN, ConversionOptions.defaults());

        ConversionResult conversionResult = new ConversionResult("# Title".getBytes(), "report.md", "text/markdown");
        InlineResult expectedResult = new InlineResult("# Title".getBytes(), "report.md", "text/markdown");

        when(pdfConverter.convert(job)).thenReturn(conversionResult);
        when(fileStorage.store(conversionResult)).thenReturn(expectedResult);

        StorageResult result = service.convert(job);

        assertThat(result).isEqualTo(expectedResult);
        verify(pdfConverter).convert(job);
        verify(fileStorage).store(conversionResult);
    }

    @Test
    void convert_returnsUrlResultWhenStorageRetainsFile() {
        byte[] pdfBytes = "fake-pdf".getBytes();
        ConversionJob job = new ConversionJob(pdfBytes, "report.pdf", OutputFormat.JSON, ConversionOptions.defaults());

        ConversionResult conversionResult = new ConversionResult("{\"pages\":[]}".getBytes(), "report.json", "application/json");
        UrlResult urlResult = new UrlResult("http://localhost:8080/api/v1/files/uuid_report.json", "report.json", "application/json");

        when(pdfConverter.convert(job)).thenReturn(conversionResult);
        when(fileStorage.store(conversionResult)).thenReturn(urlResult);

        StorageResult result = service.convert(job);

        assertThat(result).isInstanceOf(UrlResult.class);
        assertThat(((UrlResult) result).url()).contains("report.json");
    }

    @Test
    void convert_preservesAllOutputFormats() {
        for (OutputFormat format : OutputFormat.values()) {
            byte[] pdfBytes = "fake-pdf".getBytes();
            ConversionJob job = new ConversionJob(pdfBytes, "test.pdf", format, ConversionOptions.defaults());

            ConversionResult conversionResult = new ConversionResult(
                    new byte[]{1, 2, 3},
                    "test" + format.getFileExtension(),
                    format.getMediaType()
            );
            InlineResult inlineResult = new InlineResult(
                    new byte[]{1, 2, 3},
                    "test" + format.getFileExtension(),
                    format.getMediaType()
            );

            when(pdfConverter.convert(any())).thenReturn(conversionResult);
            when(fileStorage.store(conversionResult)).thenReturn(inlineResult);

            StorageResult result = service.convert(job);
            assertThat(result).isNotNull();
        }
    }
}
