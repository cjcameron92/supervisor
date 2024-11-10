repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(project(":supervisor-core"))
    compileOnly(project(":supervisor-reflection"))
    compileOnly(project(":supervisor-repository"))
    compileOnly(project(":supervisor-repository-mongo"))
    compileOnly(project(":supervisor-repository-bukkit"))
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation("org.mongodb:mongodb-driver-sync:4.7.0")

}