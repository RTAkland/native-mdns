/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/29/25
 */


package test

import cn.rtast.nmdns.broadcast
import cn.rtast.nmdns.createService
import cn.rtast.nmdns.sleep1
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class LinuxTest {
    @Test
    fun `test on linux`() {
        runBlocking {
            val service = createService(
                serviceType = "_airplay._tcp.local.",
                serviceName = "AP@RTAST",
                hostname = "rtakland.local.",
                ipAddress = "192.168.10.104",
                port = 7001,
                bindAddress = "192.168.10.104",
                mdnsPort = 5356
            )
            while (true) {
                service.broadcast()
                sleep1(2)
            }
        }
    }
}