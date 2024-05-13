import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val kotlin_version: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

apply {
    plugin("org.jetbrains.kotlin.jvm")
    plugin("com.github.johnrengelman.shadow")
}

group = "fun.fifu.mininglist"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    google()
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
}

tasks.withType<Jar>{
    manifest{
        attributes["Main-Class"] = "fun.fifu.mininglist.Main"
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
    }
}
