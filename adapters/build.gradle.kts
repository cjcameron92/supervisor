plugins {
    id("java")
}

group = "gg.supervisor.adapters"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":api"))
}

tasks.test {
    useJUnitPlatform()
}