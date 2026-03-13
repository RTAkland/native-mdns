/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/13
 */


package cn.rtast.nmdns.util


public fun htons(value: UShort): UShort =
    (((value.toInt() and 0xFF) shl 8) or ((value.toInt() ushr 8) and 0xFF)).toUShort()