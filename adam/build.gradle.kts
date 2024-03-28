plugins {
	java
	id("org.springframework.boot") version "2.6.5"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.finance"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	implementation("mysql:mysql-connector-java:8.0.33")
	// https://mvnrepository.com/artifact/com.opencsv/opencsv
	implementation("com.opencsv:opencsv:5.7.1")
	// https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
	implementation("org.apache.httpcomponents:httpclient:4.5.13")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
