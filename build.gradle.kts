import groovy.lang.Closure

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("com.palantir.git-version") version "3.0.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
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
    implementation("org.graphstream:gs-ui-swing:2.0")
    implementation("net.java.dev.jna:jna:5.12.1")
    implementation("net.java.dev.jna:jna-platform:5.12.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveClassifier.set("") // Set classifier to empty to replace the default JAR
        mergeServiceFiles() // Merge service files
        exclude("/META-INF/LICENSE") // Exclude any LICENSE files from dependencies to avoid duplicate files
        into("/META-INF") {
            from(rootProject.file("LICENSE")) // Include dendroloj license
        }
        from(sourceSets.main.get().allSource) // Include sources for API documentation and source viewing in IDE-s
        manifest {
            attributes(
                "Manifest-Version" to "1.0",
                "Implementation-Title" to "dendroloj",
                "Implementation-Version" to project.version,
            )
        }
    }
}
