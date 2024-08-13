plugins {
    java

}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":api"))
    implementation(project(":adapters"))
}

tasks.shadowJar {
    minimize()
}

