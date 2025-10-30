# native-mdns

A Kotlin multiplatform library for mdns announcer(mdns server), only broadcast is supported, 

Also, nmdns can be compiled to shared/static lib for other languages calling, see [c++ example](#cpp)

mDNS is usually used for Apple's AirPlay protocol family, there's an example in README.md [How to use](#How-to-use) 

Supported platforms:
1. windows64(mingwx64)
2. linuxX64
3. linuxArm64
4. macosArm64
5. macosX64
6. jvm(11 or later)

Supported record type
1. A
2. PTR
3. SRV
4. TXT

Supported features
1. Event hooking(Kotlin only)

> AAAA is not supported

AirPlay2(_airplay._tcp.local.) is tested

# How to use

> Make sure you added the mavenCentral repository in your project

```kotlin
kotlin {
    linuxX64()
    // Add other platforms

    sourceSets {
        commonMain.dependencies {
            implementation("cn.rtast.nmdns:native-mdns:<version>")
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
    txtRecords = listOf("key1=value1", "key2=value2")
) {
    // This block is optional
    // Event hooking in this block
    onBroadcast {
        println("broadcasted")
    }

    onRegister {
        println("reg")
    }

    onUnregistered {
        println("unreg")
    }
}
while (true) {
    service.broadcast()
    delay(2000L) // or use cn.rtast.nmdns.sleep1(2) sleep 2 seconds, this function is not a suspended function
}
```

# cpp

```cpp
#include <csignal>
#include <cstdio>
#include "libnmdns_api.h"

volatile sig_atomic_t keepRunning = 1;

void handle_sigint(int sig) {
    std::printf("\nCaught signal %d, exiting...\n", sig);
    keepRunning = 0;
}

int main() {
    libnmdns_kref_kotlin_collections_List txt = create_txt_records();

    add_txt_record(txt, "deviceid=ef:42:0d:76:f1:97");
    add_txt_record(txt, "model=AppleTV3,2C");
    add_txt_record(txt, "features=0x5A7FFFF7,0x1E");
    add_txt_record(txt, "srcvers=220.68");
    add_txt_record(txt, "pk=f3769a660475d27b4f6040381d784645e13e21c53e6d2da6a8c3d757086fc336");

    libnmdns_kref_cn_rtast_nmdns_NMDNSAnnouncer service = register_service(
        "_airplay._tcp.local.",
        "AP@RTAST",
        "rtakland.local.",
        "192.168.10.104",
        7001, 3652, "192.168.10.104",
        txt
    );

    std::signal(SIGINT, handle_sigint); 
    std::signal(SIGTERM, handle_sigint);
    std::signal(SIGHUP, handle_sigint);
    std::signal(SIGQUIT, handle_sigint);

    std::printf("Running... Press Ctrl+C to exit\n");

    while (keepRunning) {
        broadcast(service);
        sleep1(2);
    }
    return 0;
}
```
