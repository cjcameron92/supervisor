plugins {
    java

}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
}

tasks.shadowJar {
    minimize()
}

