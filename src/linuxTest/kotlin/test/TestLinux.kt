/*
* Copyright © 2025 RTAkland
* Author: RTAkland
* Date: 9/29/25
*/


package test

import cn.rtast.nmdns.onBroadcast
import cn.rtast.nmdns.onRegister
import cn.rtast.nmdns.onUnregistered
import cn.rtast.nmdns.randomMacAddress
import cn.rtast.nmdns.registerService
import cn.rtast.nmdns.sleep1
import kotlin.test.Test

class TestLinux {
    @Test
    fun `test on linux`() {
        val service = registerService(
            serviceType = "_airplay._tcp.local.",
            serviceName = "AP@RTAST",
            hostname = "rtakland.local.",
            ipAddress = "192.168.10.104",
            port = 7001,
            bindAddress = "192.168.10.104",
            mdnsPort = 5356,
            txtRecords = listOf(
                "deviceid=${randomMacAddress()}",
                "model=AppleTV3,2C",
                "features=0x5A7FFFF7,0x1E",
                "srcvers=220.68",
                "pk=f3769a660475d27b4f6040381d784645e13e21c53e6d2da6a8c3d757086fc336"
            )
        ) {
            onBroadcast {
                println(111)
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
            sleep1(2)
        }
    }
}