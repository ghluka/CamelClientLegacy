plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    strictExtraMappings.set(true)
    // FOR ALL NEW VERSIONS ENSURE TO UPDATE settings.gradle.kts !

    "1.21.8-fabric"(1_21_08, "yarn") {
        "1.21.5-fabric"(1_21_05, "yarn")
    }
}
