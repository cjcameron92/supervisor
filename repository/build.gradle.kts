plugins {
    java

}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))
    implementation(project(":adapters"))
}

tasks.shadowJar {
    minimize()
}

