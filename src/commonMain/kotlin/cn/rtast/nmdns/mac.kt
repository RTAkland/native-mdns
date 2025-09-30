/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/29/25
 */


package cn.rtast.nmdns

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.CName
import kotlin.random.Random

/**
 * generate random mac address
 */
@OptIn(ExperimentalNativeApi::class)
@CName("random_mac_address")
public fun randomMacAddress(): String {
    val bytes = ByteArray(6) { Random.nextInt(0, 256).toByte() }
    return bytes.joinToString(":") { it.toHexString() }
}
