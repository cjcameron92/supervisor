plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:5.0.0")
}

tasks.shadowJar {
    minimize()
}