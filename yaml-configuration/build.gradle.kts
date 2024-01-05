plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":api"))
    implementation(project(":configuration"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.0")

}