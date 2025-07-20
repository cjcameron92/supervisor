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

    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
}

tasks.shadowJar {
    minimize()

    relocate("com.fasterxml.jackson", "gg.supervisor.jackson")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}