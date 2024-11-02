plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
}

tasks.shadowJar {
    minimize()
}