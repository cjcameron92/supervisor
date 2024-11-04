plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

group = "gg.supervisor"
version = "1.0.1"

allprojects {

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven {
            name = "ReposiliteServer"
            url = uri("https://your-reposilite-server.com/maven") // Replace with your Reposilite server URL
            // Credentials are optional; remove if Reposilite does not require them
            credentials {
                username = System.getenv("REPOSILITE_USERNAME") ?: "your_username"
                password = System.getenv("REPOSILITE_PASSWORD") ?: "your_password"
            }
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")

    version = "1.0.2"

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                val shadowJarTask =
                    tasks.findByName("shadowJar") as? com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
                if (shadowJarTask != null) {
                    artifact(shadowJarTask.archiveFile) {
                        classifier = null
                    }
                } else {
                    from(components["java"])
                }
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }

        repositories {
            maven {
                name = "ReposiliteServer"
                url = uri("https://your-reposilite-server.com/maven") // Replace with your Reposilite server URL
                // Optional: Set credentials if required by Reposilite
                credentials {
                    username = System.getenv("REPOSILITE_USERNAME") ?: "cjcameron92"
                    password = System.getenv("REPOSILITE_PASSWORD") ?: "D43d6Ena3Mc8s7ElU+Bld4FsGE2usNsVyUxo6Z4a7+3hAl8b3LiZ8bR13Unvx70p"
                }
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Adjust this to your desired Java version
    }
}
