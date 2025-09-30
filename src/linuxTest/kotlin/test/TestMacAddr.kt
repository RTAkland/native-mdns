/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/30/25
 */


package test

import cn.rtast.nmdns.randomMacAddress
import kotlin.test.Test

class TestMacAddr {

    @Test
    fun `test generate mac address`() {
        println(randomMacAddress())
    }
}