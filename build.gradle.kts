import org.flywaydb.gradle.task.FlywayMigrateTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	kotlin("plugin.jpa") version "1.2.71"
	id("org.springframework.boot") version "2.1.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	kotlin("jvm") version "1.2.71"
	kotlin("plugin.spring") version "1.2.71"
	id("com.palantir.docker") version "0.22.1"
	id("org.flywaydb.flyway") version "5.2.4"
}

group = "org.elaastic.questions"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
//	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-allopen")
	implementation("org.springframework.data:spring-data-rest-hal-browser")
	// runtimeOnly("mysql:mysql-connector-java")
	runtime("mysql:mysql-connector-java")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "junit")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api")
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

tasks.register<Copy>("unpack") {
	dependsOn("bootJar")
	from(zipTree(bootJar.outputs.files.singleFile))
	into("build/dependency")
}

val unpack by tasks

docker {
	name = project.group.toString() + "/" + bootJar.archiveBaseName.get()
	copySpec.from(unpack.outputs).into("dependency")
	buildArgs(mapOf("DEPENDENCY" to "dependency"))
}

flyway {
	url = "jdbc:mysql://127.0.0.1:6603/elaastic-questions"
	user = "elaastic"
	password = "elaastic"
}

tasks.register<FlywayMigrateTask>("migrateDatabaseDevelopment") {
	url = "jdbc:mysql://127.0.0.1:6603/elaastic-questions"
	user = "elaastic"
	password = "elaastic"
}

tasks.register<FlywayMigrateTask>("migrateDatabaseProduction") {
	url = "jdbc:mysql://elaastic-questions-db:3306/elaastic-questions"
	user = "elaastic"
	password = "elaastic"
}