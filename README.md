# native-mdns

A Kotlin multiplatform library for mdns announcer(mdns server), only broadcast is supported, 
it depends on ktor-network

support PTR.

Supported platforms:
1. windows64(mingwx64)
2. linuxX64
3. linuxArm64
4. macosArm64
5. jvm(11 or later)


AirPlay2(_airplay._tcp.local.) is tested

# How to use

Add maven repository

```kotlin
repositories {
    mavenCentral()
    maven("https://repo.maven.rtast.cn/releases")
}
```

Add dependency

```kotlin
kotlin {
    linuxX64()
    // Add other platforms

    sourceSets {
        commonMain.dependencies {
            implementation("cn.rtast.nmdns:native-mdns:0.0.1")
        }
    }
}
```