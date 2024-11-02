plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":configuration"))
    compileOnly(project(":yaml-configuration"))
    implementation("org.mongodb:mongodb-driver-sync:4.7.0")
}

tasks.shadowJar {
    minimize()
}