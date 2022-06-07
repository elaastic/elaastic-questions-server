import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("plugin.jpa") version "1.4.31"
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    war
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.spring") version "1.4.31"
    id("com.palantir.docker") version "0.25.0"
}

group = "org.elaastic.questions"
version = "5.1.3"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.hibernate:hibernate-jcache")
    implementation("org.ehcache:ehcache:3.6.3")
//	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-cas")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("org.springframework.boot:spring-boot-starter-web")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-allopen")
    implementation("org.springframework.data:spring-data-rest-hal-explorer")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("com.icegreen:greenmail:1.5.10")
    implementation(files("lib/ApacheJMeter_oauth-v2.jar"))
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("commons-io:commons-io:2.6")
    implementation("org.apache.commons:commons-csv:1.5")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.togglz:togglz-spring-boot-starter:3.0.0")
    implementation("org.togglz:togglz-spring-security:3.0.0")
    implementation("org.togglz:togglz-kotlin:3.0.0")
    implementation("org.togglz:togglz-console:3.0.0")


    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.hamcrest:hamcrest-library")
    testImplementation("org.exparity:hamcrest-date:1.1.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
    testImplementation("org.mockito:mockito-inline:2.13.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

apply(plugin = "com.palantir.docker")

val bootJar: BootJar by tasks

docker {
    name = "elaastic-questions-server-standalone:latest"
    copySpec.from(bootJar.outputs.files.singleFile)
            .from("docker-resources/elaastic-questions/elaastic-questions.properties")
            .into("docker-build")
    buildArgs(mapOf(
            "JAR_FILE" to "docker-build/${bootJar.archiveFileName.get()}",
            "CONF_FILE" to "docker-build/elaastic-questions.properties"
    ))
}

tasks.getByName<War>("war") {
    enabled = true
}
