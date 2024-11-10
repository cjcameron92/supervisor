dependencies {
    compileOnly(project(":supervisor-core"))
    compileOnly(project(":supervisor-reflection"))
    compileOnly(project(":supervisor-repository"))
    compileOnly("com.google.code.gson:gson:2.10.1")
    implementation("org.mongodb:mongodb-driver-sync:4.7.0")

}