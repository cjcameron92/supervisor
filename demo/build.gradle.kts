
dependencies {
    compileOnly(project(":api"))
    implementation(project(":loader"))
    compileOnly(project(":configuration"))
    implementation(project(":yaml-configuration"))
    implementation(project(":storage"))
    implementation(project(":items"))
    implementation(project(":menu"))


}

tasks.shadowJar {
    minimize()
}

