plugins {
    java
}

version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":configuration"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
}

tasks.shadowJar {
    minimize()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Adjust this to your desired Java version
    }
}