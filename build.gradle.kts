import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "sollecitom.examples"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.50"
}

repositories {
    mavenCentral()
}

dependencies {
    with(Versions) {
        implementation(kotlin("stdlib-jdk8"))
        implementation(group = "org.springframework.boot", name = "spring-boot", version = springBoot)
        implementation(group = "org.springframework.boot", name = "spring-boot-starter-web", version = springBoot)
        implementation(group = "org.springframework.boot", name = "spring-boot-autoconfigure", version = springBoot)
        implementation(group = "org.json", name = "json", version = json)

        testImplementation(kotlin("test"))
        testImplementation(group = "org.everit.json", name = "org.everit.json.schema", version = everitJsonSchema)
        testImplementation(group = "org.springframework.boot", name = "spring-boot-test", version = springBoot)
        testImplementation(group = "com.willowtreeapps.assertk", name = "assertk", version = assertk, ext = "pom")
        testImplementation(group = "org.mockito", name = "mockito-core", version = mockito)
        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junit)
        testRuntime(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junit)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    enabled = false
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}