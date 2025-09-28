import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.2.10"
    id("maven-publish")
}

group = "cn.rtast.nmdns"
version = "0.0.2"

repositories {
    mavenCentral()
}

kotlin {
    val nativeTargets = listOf(
        linuxArm64(),
        linuxX64(),
        mingwX64(),
        macosArm64()
    )
    jvm { compilerOptions { jvmTarget = JvmTarget.JVM_11 } }

    nativeTargets.forEach {
        it.binaries {
            staticLib {
                baseName = "nmdns"
            }
            sharedLib {
                baseName = "nmdns"
            }
        }
    }

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

/**
 * link all targets static lib
 * Apple target can only be compiled on macos
 */
tasks.register("linkStaticLibrary") {
    group = "nmdns"
    dependsOn(tasks.named("linkReleaseStaticLinuxArm64"))
    dependsOn(tasks.named("linkReleaseStaticLinuxX64"))
    dependsOn(tasks.named("linkReleaseStaticMacosArm64"))
    dependsOn(tasks.named("linkReleaseStaticMingwX64"))
}

/**
 * link all targrts to shared lib
 * Apple target can only be compiled on macos
 */
tasks.register("linkSharedLibrary") {
    group = "nmdns"
    dependsOn(tasks.named("linkReleaseSharedLinuxArm64"))
    dependsOn(tasks.named("linkReleaseSharedLinuxX64"))
    dependsOn(tasks.named("linkReleaseSharedMacosArm64"))
    dependsOn(tasks.named("linkReleaseSharedMingwX64"))
}