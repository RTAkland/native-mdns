import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.2.0"
    id("com.vanniktech.maven.publish") version "0.31.0-rc2"
    id("signing")
}

val libVersion: String by project

group = "cn.rtast.nmdns"
version = libVersion

repositories {
    mavenCentral()
}

kotlin {
    val nativeTargets = listOf(
        linuxArm64(),
        linuxX64(),
        mingwX64(),
        macosArm64(),
        macosX64()
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

    compilerOptions { freeCompilerArgs.addAll("-Xexpect-actual-classes") }

    explicitApi()

    sourceSets {
        commonMain.dependencies {

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

mavenPublishing {
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
            mavenCentral()
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (System.getenv("PUBLISH_TOKEN") == null) signAllPublications()
    coordinates("cn.rtast.nmdns", project.name, libVersion)
    pom {
        name = project.name
        description = "mDNS in kotlin multiplatform"
        url = "https://github.com/RTAkland/native-mdns"
        developers {
            developer {
                id = "rtakland"
                name = "RTAkland"
                email = "rtakland@outlook.com"
            }
        }

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        scm {
            url = "https://github.com/RTAkland/native-mdns"
            connection = "scm:git:git://github.com/RTAkland/native-mdns.git"
            developerConnection = "scm:git:ssh://git@github.com/RTAkland/native-mdns.git"
        }
    }
}

if (System.getenv("PUBLISH_TOKEN") == null) {
    signing {
        useGpgCmd()
        sign(publishing.publications)
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