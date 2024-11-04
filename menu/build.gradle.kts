version = "1.0.0"


dependencies {
    implementation(project(":util"))
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.shadowJar {
    minimize()
}
