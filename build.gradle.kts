import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.2.10"
    id("maven-publish")
}

group = "cn.rtast.nmdns"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    linuxArm64()
    linuxX64()
    mingwX64()
    macosArm64()
    jvm { compilerOptions { jvmTarget = JvmTarget.JVM_11 } }

    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-network:3.2.3")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

publishing {
    repositories {
        mavenLocal()
        maven("https://repo.maven.rtast.cn/releases") {
            name = "RTAST"
            credentials {
                username = "RTAkland"
                password = System.getenv("PUBLISH_TOKEN")
            }
        }
    }
}
