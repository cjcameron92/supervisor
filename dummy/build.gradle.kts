plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":yaml-configuration"))
    implementation(project(":mongo-repository"))
    implementation(project(":itemstack-repository"))


}

tasks.shadowJar {


}