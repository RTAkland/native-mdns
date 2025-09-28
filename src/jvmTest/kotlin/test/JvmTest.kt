/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/29/25
 */


package test

import cn.rtast.nmdns.broadcast
import cn.rtast.nmdns.randomMacAddress
import cn.rtast.nmdns.registerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class JvmTest {
    @Test
    fun `test on jvm`() {
        runBlocking {
//            val manager = SelectorManager(Dispatchers.Default)
//            val serverSocket = aSocket(manager).udp()
//                .bind(InetSocketAddress("192.168.10.104", 5356))
//            val txtRecords = listOf(
//                "deviceid=01:23:45:67:89:AB",
//                "model=AppleTV3,2C",
//                "features=0x5A7FFFF7,0x1E",
//                "srcvers=220.68",
//                "pk=f3769a660475d27b4f6040381d784645e13e21c53e6d2da6a8c3d757086fc336"
//            )
//            val packet = buildPacket(
//                serviceType = "_airplay._tcp.local.",
//                serviceName = "MyService._airplay._tcp.local.",
//                hostname = "rtakland.local.",
//                ip = "192.168.10.104",
//                port = 8080,
//                txtRecords = txtRecords
//            )
//            while (true) {
//                serverSocket.send(Datagram(packet.copy(), InetSocketAddress("224.0.0.251", 5353)))
//                println("sent")
//                delay(2000L)
//            }
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
            )
            while (true) {
                service.broadcast()
                delay(2000L)
            }
        }
    }
}