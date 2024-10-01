plugins {
    java
}

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