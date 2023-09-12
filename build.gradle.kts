import groovy.lang.Closure

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.palantir.git-version") version "3.0.0"
}

group = "ee.ut.dendroloj"
val gitVersion: Closure<String> by extra
version = gitVersion()

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.14.4")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.4")
    implementation("org.graphstream:gs-core:2.0")
    implementation("org.graphstream:gs-ui-javafx:2.0")
    implementation("net.java.dev.jna:jna:5.12.1")
    implementation("net.java.dev.jna:jna-platform:5.12.1")
    implementation("org.openjfx:javafx-graphics:13.0.2:win")
    implementation("org.openjfx:javafx-graphics:13.0.2:linux")
    implementation("org.openjfx:javafx-graphics:13.0.2:mac")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

javafx {
    version = "13"
    modules("javafx.controls", "javafx.fxml")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveClassifier.set("") // Set classifier to empty to replace the default JAR
        mergeServiceFiles() // Merge service files
        manifest {
            attributes(
                    "Manifest-Version" to "1.0",
                    "Implementation-Version" to project.version,
            )
        }
    }
}

//TODO: targetCompatibility