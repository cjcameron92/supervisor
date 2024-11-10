plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("maven-publish")
}

group = "com.vertmix.supervisor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.md-5.net/content/repositories/snapshots/") // BungeeCord repo
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        maven("https://repo.md-5.net/content/repositories/snapshots/") // BungeeCord repo
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(project(":supervisor-core"))
    implementation(project(":supervisor-loader"))
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
}
