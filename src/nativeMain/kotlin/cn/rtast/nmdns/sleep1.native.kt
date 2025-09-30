/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/29/25
 */

package cn.rtast.nmdns

import platform.posix.sleep
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
@CName("sleep1")
public actual fun sleep1(time: Int) {
    sleep(time.toUInt())
}