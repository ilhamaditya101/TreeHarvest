plugins {
    java
    id("com.gradleup.shadow") version "9.0.0"
}

group = "id.yeue"
version = "1.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    // AuraSkills 2.2.4 is published on Maven Central.
    compileOnly("dev.aurelium:auraskills-api:2.2.4")
    // MMOItems API is intentionally not a build dependency.
    // TreeHarvest uses configurable MMOItems console commands, so the build
    // does not depend on repo.lumine.io or a SNAPSHOT API.
}

java { toolchain.languageVersion.set(JavaLanguageVersion.of(17)) }
tasks {
    shadowJar { archiveClassifier.set("") }
    build { dependsOn(shadowJar) }
}
