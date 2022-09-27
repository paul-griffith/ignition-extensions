plugins {
    `java-library`
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    compileOnly(libs.ignition.common)
    testImplementation(libs.ignition.common)
    testImplementation(libs.bundles.kotest)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
