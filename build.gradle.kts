@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modl)
}

subprojects {
    // cascade version, which will be set at command line in CI, down to subprojects
    version = rootProject.version
}

ignitionModule {
    name.set("Ignition Extensions")
    fileName.set("Ignition-Extensions.modl")
    id.set("org.imdc.extensions.IgnitionExtensions")
    moduleVersion.set("${project.version}")
    moduleDescription.set("Useful but niche extensions to Ignition for power users")
    license.set("LICENSE.md")
    requiredIgnitionVersion.set(libs.versions.ignition.get())

    projectScopes.set(
        mapOf(
            projects.client.name to "C",
            projects.common.name to "GDC",
            projects.designer.name to "D",
            projects.gateway.name to "G",
        ),
    )

    hooks.set(
        mapOf(
            "org.imdc.extensions.gateway.GatewayHook" to "G",
            "org.imdc.extensions.designer.DesignerHook" to "D",
            "org.imdc.extensions.client.ClientHook" to "C",
        ),
    )

    skipModlSigning.set(!findProperty("signModule").toString().toBoolean())
}

tasks.deployModl {
    hostGateway.set("http://localhost:18088")
}
