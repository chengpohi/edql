rootProject.name = "edql"

include(
        "modules:core",
        "modules:repl",
        "modules:script"
)

pluginManagement {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
    }
}