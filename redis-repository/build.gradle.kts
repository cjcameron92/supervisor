plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:5.0.0")
    implementation(project(":repository"))
    compileOnly(project(":core"))
    compileOnly(project(":adapters"))

}

tasks.shadowJar {
    minimize()
}