plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.threemeals"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
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

    // Basic
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // DB connector
    runtimeOnly("mysql:mysql-connector-java:8.0.34")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta") // Querydsl의 JPA 관련 기능 제공 (JPAQueryFactory, BooleanBuilder, PathBuilder ...)
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta") // QueryDSL APT(Annotation Processing Tool)로 컴파일 시점에 @Entity 애노테이션이 붙은 JPA 엔티티 클래스를 기반으로 Q 클래스를 생성
    annotationProcessor("jakarta.annotation:jakarta.annotation-api") // QueryDSL APT에서 사용하는 @Generated 애노테이션을 제공.  클래스 생성 시 @Generated 애노테이션이 필요하며, 이를 처리하기 위해 이 의존성이 필요
    annotationProcessor("jakarta.persistence:jakarta.persistence-api") // JPA 표준 애노테이션(@Entity, @Id, @Column 등)을 정의. QueryDSL APT가 엔티티 클래스를 분석하고 Q 클래스를 생성하는 데 필수

    testImplementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    testAnnotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    testAnnotationProcessor("jakarta.annotation:jakarta.annotation-api")
    testAnnotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Redis
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // BCrypt Encoder (Spring Security 의존성 추가 없이 Encoder 사용할 때)
    implementation("at.favre.lib:bcrypt:0.10.2")

    // JWT
    implementation("io.jsonwebtoken:jjwt:0.9.1") // Java JWT library
    implementation("javax.xml.bind:jaxb-api:2.3.1") // XML document와 Java 객체 간 매핑 자동화

}

tasks.withType<Test> {
    useJUnitPlatform()
}
