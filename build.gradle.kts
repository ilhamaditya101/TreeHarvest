plugins {
    java
    id("com.gradleup.shadow") version "9.0.0"
}

group = "id.yeue"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.lumine.io/repository/maven-public/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.2.4")
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")
}

java { toolchain.languageVersion.set(JavaLanguageVersion.of(17)) }
tasks {
    shadowJar { archiveClassifier.set("") }
    build { dependsOn(shadowJar) }
}
