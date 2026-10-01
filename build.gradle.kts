import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

group = "carpet"
version = (findProperty("pluginVersion") as String?)?.trim()?.takeIf { it.isNotEmpty() } ?: "1.0.0"
description = "Carpet Mod ported to Leaf"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

paperweight {
    // Leaf/Paper 1.21.11 ships a Mojang-mapped runtime. Do not reobfuscate.
    reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
        options.compilerArgs.add("-Xlint:-options")
        options.compilerArgs.add("-Xlint:-deprecation")
        options.compilerArgs.add("-Xlint:-removal")
    }

    processResources {
        val props = mapOf("projectVersion" to version)
        inputs.properties(props)
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    jar {
        archiveBaseName.set("leafcarpet")
        archiveClassifier.set("")
        manifest {
            // plugin.yml plugins are assumed Spigot-mapped unless this is set.
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
}
