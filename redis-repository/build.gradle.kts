plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:5.0.0")
    implementation(project(":repository"))
    implementation(project(":api"))

}

tasks.shadowJar {
    minimize()
}