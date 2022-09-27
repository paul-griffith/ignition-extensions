@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modl)
    // alias(libs.plugins.dokka) TODO: Investigate Dokka for automatic generation of module docs
}

subprojects {
    // cascade version, which will be set at command line in CI, down to subprojects
    version = rootProject.version
}

ignitionModule {
    name.set("Ignition Extensions")
    fileName.set("Ignition-Extensions.modl")
    id.set("io.github.paulgriffith.extensions.IgnitionExtensions")
    moduleVersion.set("${project.version}")

    moduleDescription.set("Useful but niche extensions to Ignition for power users")
    requiredIgnitionVersion.set(libs.versions.ignition.get())

    projectScopes.putAll(
        mapOf(
            ":client" to "C",
            ":common" to "GDC",
            ":designer" to "D",
            ":gateway" to "G",
        ),
    )

    moduleDependencies.set(
        mapOf(
            "com.inductiveautomation.perspective" to "G",
        ),
    )

    hooks.putAll(
        mapOf(
            "io.github.paulgriffith.extensions.gateway.GatewayHook" to "G",
            "io.github.paulgriffith.extensions.designer.DesignerHook" to "D",
            "io.github.paulgriffith.extensions.client.ClientHook" to "C",
        ),
    )

    skipModlSigning.set(true) // TODO signing
}
