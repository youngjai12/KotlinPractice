import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.7"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "com.brandon"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

configurations.all {
	exclude("org.slf4j", "slf4j-reload4j")
	exclude("org.slf4j", "slf4j-jcl")
	exclude("org.apache.logging.log4j", "log4j-to-slf4j")
	exclude("org.apache.logging.log4j", "log4j-slf4j-impl")
	exclude("log4j", "log4j")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("mysql:mysql-connector-java:8.0.26")

	implementation("org.slf4j:jcl-over-slf4j")
	implementation("org.slf4j:jul-to-slf4j")
	implementation("org.slf4j:log4j-over-slf4j")
	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")

	if (System.getProperty("os.name") == "Mac OS X" && System.getProperty("os.arch") == "aarch64") {
		implementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")
	}

	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
	implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.20")
	implementation("org.jetbrains.kotlin:kotlin-noarg:1.9.20")
	implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.0")
	implementation("org.springframework.boot:spring-boot-gradle-plugin:3.1.5")

	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mock-server:mockserver-netty:5.11.1")
	testImplementation("org.mock-server:mockserver-client-java:5.11.1")
	testImplementation("com.ninja-squad:springmockk:3.1.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}