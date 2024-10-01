plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

group = "gg.llamas.supervisor"
version = "1.0"


allprojects {
    repositories {
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.aikar.co/content/groups/aikar/")
    }

}

val projectsToPublish = listOf("loader", "yaml-configuration")


subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    }



    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                // Use either shadowJar or jar for the artifact, depending on your project's requirements
                val shadowJarTask = tasks.findByName("shadowJar") as? com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
                if (shadowJarTask != null) {
                    artifact(shadowJarTask.archiveFile.get().asFile) {
                        classifier = null
                    }
                } else {
                    from(components["java"])
                }

                // Set the groupId, version, and artifactId dynamically
                groupId = project.group.toString()
                // Use the project directory's name for the artifactId
                artifactId = project.name
                version = project.version.toString()

                // Optional: Print out the artifact details for confirmation
                println("Configuring publication for ${project.name} with artifactId ${project.name}")
            }
        }
    }

}
