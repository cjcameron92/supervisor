dependencies {
    implementation(project(":api"))
    implementation(project(":loader"))
    implementation(project(":yaml-configuration"))
}

tasks.shadowJar {
    archiveBaseName.set("supervisor-bundle.jar")
    archiveClassifier.set(null as String?)
    archiveVersion.set("1.0")
}