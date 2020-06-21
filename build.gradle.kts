import java.net.URI

plugins {
    application
}

group = "com.github.korotovskih"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("info.picocli", "picocli", "4.3.2")
    implementation("com.github.1c-syntax", "bsl-parser", "0.14.1") {
        exclude("com.tunnelvisionlabs", "antlr4-annotations")
        exclude("com.ibm.icu", "*")
        exclude("org.antlr", "ST4")
        exclude("org.abego.treelayout", "org.abego.treelayout.core")
        exclude("org.antlr", "antlr-runtime")
        exclude("org.glassfish", "javax.json")
    }
}

application {
    mainClassName = "com.github.korotovskih.bsl.GenericReport"
}