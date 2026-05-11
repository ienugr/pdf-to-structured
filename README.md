# pdf-to-structured

A Spring Boot REST API that converts PDF files into structured formats — Markdown, JSON, HTML, plain text, and more — powered by [OpenDataLoader PDF](https://github.com/opendataloader-project/opendataloader-pdf).

## Features

- **Multiple output formats** — Markdown, JSON, HTML, plain text, tagged PDF, and Markdown variants with embedded HTML or images
- **Password-protected PDFs** — pass the password as a request parameter
- **Page range selection** — convert specific pages rather than the whole document
- **Pluggable storage** — return converted bytes directly (passthrough, default) or persist files to local disk and get back a download URL
- **RFC 7807 error responses** — structured, machine-readable `ProblemDetail` JSON for all errors
- **Virtual threads** — runs on Java 21 virtual threads for efficient I/O-bound workloads

## Requirements

- Java 21+
- Gradle 8.x

## Getting started

```bash
git clone https://github.com/ienugr/pdf-to-structured.git
cd pdf-to-structured
./gradlew bootRun
```

The server starts on port `8080`.

## API

### `POST /api/v1/convert`

Converts a PDF file to the requested format.

**Content-Type:** `multipart/form-data`

| Parameter           | Type    | Required | Default  | Description                                                                                       |
|---------------------|---------|----------|----------|---------------------------------------------------------------------------------------------------|
| `file`              | file    | yes      | —        | The PDF file to convert                                                                           |
| `format`            | string  | yes      | —        | Output format (see [Supported formats](#supported-formats))                                       |
| `password`          | string  | no       | —        | Password for encrypted PDFs                                                                       |
| `pages`             | string  | no       | all      | Page range, e.g. `1,3,5-7`                                                                       |
| `keepLineBreaks`    | boolean | no       | `false`  | Preserve original line breaks                                                                     |
| `includeHeaderFooter` | boolean | no     | `false`  | Include page headers and footers                                                                  |
| `readingOrder`      | string  | no       | `xycut`  | Reading order algorithm: `xycut` or `off`                                                        |
| `tableMethod`       | string  | no       | `default` | Table detection method: `default` or `cluster`                                                  |

#### Supported formats

| `format` value        | Response content-type | Description                              |
|-----------------------|-----------------------|------------------------------------------|
| `json`                | `application/json`    | Structured JSON representation           |
| `text`                | `text/plain`          | Plain text extraction                    |
| `html`                | `text/html`           | HTML with layout preserved               |
| `pdf`                 | `application/pdf`     | Re-processed PDF                         |
| `markdown`            | `text/markdown`       | Clean Markdown                           |
| `markdown-with-html`  | `text/markdown`       | Markdown with inline HTML elements       |
| `markdown-with-images`| `text/markdown`       | Markdown with embedded base64 images     |
| `tagged-pdf`          | `application/pdf`     | Accessibility-tagged PDF                 |

#### Response — passthrough storage (default)

The converted file is returned as a binary download:

```
HTTP/1.1 200 OK
Content-Type: text/markdown
Content-Disposition: attachment; filename="document.md"

# My Document
...
```

#### Response — local storage

When `app.storage.type=local`, a JSON body with a download URL is returned instead:

```json
{
  "url": "http://localhost:8080/api/v1/files/document.md",
  "filename": "document.md",
  "mediaType": "text/markdown"
}
```

#### Error responses

All errors follow [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807) (`application/problem+json`):

```json
{
  "type": "https://pdf-converter/errors/unsupported-format",
  "title": "Unsupported Output Format",
  "status": 400,
  "detail": "Format 'docx' is not supported.",
  "requestedFormat": "docx"
}
```

| Scenario                        | HTTP status |
|---------------------------------|-------------|
| Unknown format value            | `400`       |
| Invalid request argument        | `400`       |
| File exceeds size limit (50 MB) | `413`       |
| Conversion failed               | `422`       |
| Storage error                   | `500`       |

#### Example — curl

```bash
# Convert to Markdown (passthrough — file downloaded directly)
curl -X POST http://localhost:8080/api/v1/convert \
  -F "file=@report.pdf" \
  -F "format=markdown" \
  -o report.md

# Convert specific pages of a password-protected PDF to JSON
curl -X POST http://localhost:8080/api/v1/convert \
  -F "file=@report.pdf" \
  -F "format=json" \
  -F "password=secret" \
  -F "pages=1-5"
```

### `GET /api/v1/files/{filename}` _(local storage only)_

Downloads a previously converted file by filename. Only available when `app.storage.type=local`.

## Configuration

All settings live in `src/main/resources/application.yml`.

### Upload size limit

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB      # maximum size of a single file
      max-request-size: 55MB   # maximum total request size
```

### Storage backend

| `app.storage.type` | Behaviour |
|--------------------|-----------|
| `passthrough` (default) | Converted bytes are returned directly in the response body; nothing is written to disk |
| `local` | Converted file is saved to `app.storage.local.dir`; response contains a download URL |

```yaml
app:
  storage:
    type: local
    local:
      dir: ./uploads               # directory to store converted files
      base-url: http://localhost:8080  # base URL used to build download links
```

Or pass at startup:

```bash
./gradlew bootRun --args='--app.storage.type=local --app.storage.local.dir=/var/uploads'
```

## Architecture

The project follows a ports-and-adapters (hexagonal) layout:

```
gr.ienu.pdfconverter
├── domain/               # Core types: OutputFormat, ConversionJob, ConversionOptions, exceptions
├── converter/            # PdfConverter port + OpenDataLoader adapter
├── storage/              # FileStorage port + Passthrough and Local adapters
├── service/              # PdfConversionService — orchestrates converter + storage
└── web/                  # Spring MVC controllers and error handler
```

Storage and converter backends are swapped via `@ConditionalOnProperty` — no code changes required.

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build | Gradle 8.x (Groovy DSL) |
| PDF engine | OpenDataLoader PDF Core 2.4.3 |

## License

[Apache License 2.0](LICENSE)
