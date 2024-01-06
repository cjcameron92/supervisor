
dependencies {
    compileOnly(project(":api"))
    implementation(project(":loader"))
    compileOnly(project(":configuration"))
    implementation(project(":yaml-configuration"))


}

tasks.shadowJar {
    minimize()
}

