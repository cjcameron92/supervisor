dependencies {
    compileOnly(project(":supervisor-core"))
    compileOnly(project(":supervisor-configuration"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")

}