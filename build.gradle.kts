plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
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

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
    }
}