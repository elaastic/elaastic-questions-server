rootProject.name = "elaastic"
include("server")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("server/gradle/libs.versions.toml"))
        }
    }
}
