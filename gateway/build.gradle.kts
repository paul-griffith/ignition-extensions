plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    compileOnly(libs.bundles.gateway)
    compileOnly(projects.common)
}
