plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
	id("org.openapi.generator") version "7.5.0"
}

group = "ru.deevdenis"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()

}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.apache.commons:commons-lang3")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
