
dependencies {
    compileOnly(project(":core"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
