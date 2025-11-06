plugins {
    `java-library`
    kotlin("jvm")
}

kotlin {
    jvmToolchain {
        languageVersion = libs.versions.java.map(JavaLanguageVersion::of)
    }
}

java {
    toolchain {
        languageVersion = libs.versions.java.map(JavaLanguageVersion::of)
    }
}

dependencies {
    compileOnly(libs.bundles.gateway)
    compileOnly(projects.common)
}
