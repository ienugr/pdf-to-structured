package com.example.pdfconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class PdfConverterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfConverterApplication.class, args);
    }
}
