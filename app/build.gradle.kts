/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Scala application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.4/userguide/building_java_projects.html
 * This project uses @Incubating APIs which are subject to change.
 */
 

version = "0.1.0"

plugins {
    // Apply the scala Plugin to add support for Scala.
    scala

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    id("com.github.johnrengelman.shadow") version "7.1.2"

}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use Scala 2.13 in our library project
    implementation("org.scala-lang:scala-library:2.13.6")

    implementation("org.apache.jena:apache-jena-libs:4.5.0@pom")
    implementation("org.apache.jena:jena-core:4.5.0")
    implementation("org.apache.jena:jena-arq:4.5.0")

    // https://mvnrepository.com/artifact/com.github.scopt/scopt
    implementation("com.github.scopt:scopt_2.13:4.0.1")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")



}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit4 test framework
            useJUnit("4.13.2")

            dependencies {
                // Use Scalatest for testing our library
                implementation("org.scalatest:scalatest_2.13:3.2.10")
                implementation("org.scalatestplus:junit-4-13_2.13:3.2.2.0")

                // Need scala-xml at test runtime
                runtimeOnly("org.scala-lang.modules:scala-xml_2.13:1.2.0")
            }
        }
    }
}

application {
    // Define the main class for the application.
    mainClass.set("RMLInjector.App")
}

tasks.shadowJar{
    manifest {
        attributes(mapOf("Implementation-Title" to rootProject.name,
                         "Implementation-Version" to project.version))
    }
    archiveBaseName.set(rootProject.name)

}

tasks.jar {
    manifest {
        attributes(mapOf("Implementation-Title" to rootProject.name,
                         "Implementation-Version" to project.version))
    }
    archiveBaseName.set(rootProject.name)
}