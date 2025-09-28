plugins {
    kotlin("multiplatform") version "2.2.10"
}

group = "cn.rtast.nmdns"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    linuxArm64()
    linuxX64()
    mingwX64()
    macosArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-network:3.2.3")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
