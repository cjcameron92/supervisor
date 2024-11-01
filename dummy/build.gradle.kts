plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":yaml-configuration"))

}

tasks.shadowJar {
    minimize()
}