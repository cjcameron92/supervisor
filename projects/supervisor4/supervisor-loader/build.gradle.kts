plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation(project(":supervisor-core"))
    implementation(project(":supervisor-reflection"))
    implementation(project(":supervisor-repository"))
    implementation(project(":supervisor-repository-json"))

    implementation("org.reflections:reflections:0.10.2")
    implementation("com.google.code.gson:gson:2.10.1")

}

tasks.shadowJar {
    relocate("com.vertmix.supervisor.repository", "com.vertmix.supervisor.loader")
}
