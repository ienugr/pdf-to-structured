plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "gr.ienu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    // Required for org.verapdf:* transitive dependencies of opendataloader-pdf-core
    maven { url = uri("https://artifactory.openpreservation.org/artifactory/vera-dev") }
}

dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // PDF conversion
    implementation("org.opendataloader:opendataloader-pdf-core:2.4.3")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
