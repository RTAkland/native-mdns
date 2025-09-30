/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/30/25
 */


package test

import cn.rtast.nmdns.buildPacket
import cn.rtast.nmdns.createSocket
import cn.rtast.nmdns.randomMacAddress
import cn.rtast.nmdns.sleep1
import kotlin.test.Test

class TestNative {

    @Test
    fun `test native socket`() {
        val packet = buildPacket(
            serviceType = "_airplay._tcp.local.",
            serviceName = "AP@RTAST._airplay._tcp.local.",
            hostname = "rtakland.local.",
            ip = "192.168.10.104",
            port = 7001,
            txtRecords = listOf(
                "deviceid=${randomMacAddress()}",
                "model=AppleTV3,2C",
                "features=0x5A7FFFF7,0x1E",
                "srcvers=220.68",
                "pk=f3769a660475d27b4f6040381d784645e13e21c53e6d2da6a8c3d757086fc336"
            )
        )

        val service = createSocket()
            .bind("192.168.10.104", 4546)
        while (true) {
            service.send(packet.copyOf())
            sleep1(2)
        }
    }
}