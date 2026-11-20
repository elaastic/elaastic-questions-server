/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

ext["spring-security.version"] = libs.versions.springSecurity.get()
// Override Spring Boot 2.7's managed versions for Java 21 support.
ext["mockito.version"] = libs.versions.mockito.get()
ext["byte-buddy.version"] = libs.versions.byteBuddy.get()

plugins {
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.kotlinJpa)
    alias(libs.plugins.sonarqube)
    jacoco
}

group = "org.elaastic.questions"
version = "6.4.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

sonar {
    properties {
        property("sonar.projectKey", "elaastic_elaastic-questions-server")
        property("sonar.organization", "elaastic")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springBootStarterDataJpa)
    implementation(libs.springBootStarterWeb)
    implementation(libs.jacksonModuleKotlin)
    implementation(libs.flywayCore)
    implementation(libs.flywayMysql)
    implementation(libs.kotlinReflect)

    implementation(libs.springBootStarterValidation)
    implementation(libs.springBootStarterDataRest)
    implementation(libs.springBootStarterMail)
    implementation(libs.springBootStarterCache)
    implementation(libs.hibernateJcache)
    implementation(libs.ehcache)
    implementation(libs.springBootStarterSecurity)
    implementation(libs.springSecurityCas)
    implementation(libs.springBootStarterOauth2Client)
    implementation(libs.springBootStarterThymeleaf)
    implementation(libs.thymeleafExtrasSpringsecurity5)
    implementation(libs.jacksonDatabind)
    implementation(libs.kotlinStdlibJdk8)
    implementation(libs.kotlinAllopen)
    implementation(libs.springDataRestHalExplorer)
    implementation(libs.commonsLang3)
    implementation(libs.greenmail)
    implementation(files("lib/ApacheJMeter_oauth-v2.jar"))
    implementation(libs.gson)
    implementation(libs.commonsIo)
    implementation(libs.commonsCsv)
    implementation(libs.springBootStarterWebsocket)
    implementation(libs.togglzSpringBootStarter)
    implementation(libs.togglzSpringSecurity)
    implementation(libs.togglzKotlin)
    implementation(libs.togglzConsole)
    implementation(libs.thymeleafExtrasTogglz)
    implementation(libs.springHateoasJsonapi)
    implementation(libs.jsoup)
    implementation(libs.cucumberSpring)

    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.mockk)
    testImplementation(libs.springSecurityTest)
    testImplementation(libs.springBootStarterTest) {
        exclude(module = "junit")
    }
    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.hamcrestLibrary)
    testImplementation(libs.hamcrestDate)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoCore)

    testImplementation(libs.cucumberJava)
    testImplementation(libs.cucumberJunit)
    testImplementation(libs.cucumberJunitPlatformEngine)
    testImplementation(libs.junitPlatformSuite)

    runtimeOnly(libs.mysqlConnectorJ)

    testRuntimeOnly(libs.junitJupiterEngine)
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}



tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.register("dockerBuild") {
    group = "Docker"
    description = "Builds the Docker image aligned with the elaastic version"
    doLast {
        exec {
            commandLine("docker", "build",
                "-t", "elaastic/elaastic-questions-server:v$version",
                "-t", "elaastic/elaastic-questions-server:latest",
                ".")
        }
    }
}

