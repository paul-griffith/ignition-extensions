plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modl)
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://nexus.inductiveautomation.com/repository/public")
    }
}

subprojects {
    // cascade version, which will be set at command line in CI, down to subprojects
    version = rootProject.version
}

ignitionModule {
    name = "Ignition Extensions"
    fileName = "Ignition-Extensions.modl"
    id = "org.imdc.extensions.IgnitionExtensions"
    moduleVersion = "${project.version}"
    moduleDescription = "Useful but niche extensions to Ignition for power users"
    license = "LICENSE.txt"
    requiredIgnitionVersion = libs.versions.ignition

    projectScopes.putAll(
        mapOf(
            projects.common.dependencyProject.path to "GDC",
            projects.gateway.dependencyProject.path to "G",
            projects.designer.dependencyProject.path to "D",
            projects.client.dependencyProject.path to "C",
        ),
    )

    hooks.putAll(
        mapOf(
            "org.imdc.extensions.gateway.GatewayHook" to "G",
            "org.imdc.extensions.designer.DesignerHook" to "D",
            "org.imdc.extensions.client.ClientHook" to "C",
        ),
    )

    skipModlSigning = !findProperty("signModule").toString().toBoolean()
}

tasks.deployModl {
    hostGateway = "http://localhost:18088"
}
