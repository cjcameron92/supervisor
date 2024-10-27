
dependencies {
    compileOnly(project(":core"))

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
