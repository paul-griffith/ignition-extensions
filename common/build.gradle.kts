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
    compileOnly(libs.ignition.common)
    testImplementation(libs.ignition.common)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.mockk)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        jvmArgs = listOf("--add-opens", "java.base/java.io=ALL-UNNAMED")
    }
}
