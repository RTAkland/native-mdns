# native-mdns

A Kotlin multiplatform library for mdns announcer(mdns server), only broadcast is supported, 
it depends on ktor-network.

Also, nmdns can be compiled to shared/static lib for other language usage, see [build.gradle.kts](build.gradle.kts)

Supported platforms:
1. windows64(mingwx64)
2. linuxX64
3. linuxArm64
4. macosArm64
5. jvm(11 or later)

Supported srv type
1. A
2. PTR

> AAAA is not supported

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
            implementation("cn.rtast.nmdns:native-mdns:0.0.2")
        }
    }
}
```

There is a simple airplay2 service

```kotlin
val service = registerService(
    serviceType = "_airplay._tcp.local.",
    serviceName = "AP@RTAST",
    hostname = "rtakland.local.",
    ipAddress = "192.168.10.104",
    port = 7001,
    bindAddress = "192.168.10.104",
    mdnsPort = 5356,
)
while (true) {
    service.broadcast()
    delay(2000L) // or use cn.rtast.nmdns.sleep1(2) sleep 2 seconds
}
```