plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.openapi.generator") version "7.5.0"
	id("org.sonarqube") version "5.0.0.4638"
}

group = "ru.deevdenis"
version = "0.0.1"

val generatedSourcesDir = "$buildDir/generated/openapi"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

jacoco {
	toolVersion = "0.8.11"
	reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.apache.commons:commons-lang3")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("com.google.code.findbugs:jsr305:3.0.0")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
}

sourceSets {
	getByName("main") {
		java {
			srcDir("$generatedSourcesDir/src/main/java")
		}
	}
}

tasks {
	val openApiGenerate by getting

	val compileJava by getting {
		dependsOn(openApiGenerate)
	}
}

openApiGenerate {
	generatorName.set("java")

	inputSpec.set("$rootDir/openapi/api.yml")
	outputDir.set(generatedSourcesDir)

	apiPackage.set("org.openapi.java.api")
	invokerPackage.set("org.openapi.java.invoker")
	modelPackage.set("org.openapi.java.model")

	templateDir.set("$rootDir/openapi/templates")

	configOptions.set(
		mapOf(
			"interfaceOnly" to "false",
			"useTags" to "true",
			"delegatePatten" to "true",
			"serializableModel" to "true",
			"dateLibrary" to "java8",
			"library" to "webclient"
		)
	)
}


