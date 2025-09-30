/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/30/25
 */

@file:Suppress("unused")


package cn.rtast.nmdns

public expect class Socket1 internal constructor() {
    /**
     * bind to ip
     */
    public fun bind(ip: String, port: Int): Socket1

    /**
     * send packet
     */
    public fun send(packet: ByteArray)

    /**
     * close
     */
    public fun destroy()
}

internal fun createSocket(): Socket1 = Socket1()

public fun htons(value: UShort): UShort =
    (((value.toInt() and 0xFF) shl 8) or ((value.toInt() ushr 8) and 0xFF)).toUShort()

public fun htonl(value: UInt): UInt =
    ((value and 0xFFu) shl 24) or
            ((value and 0xFF00u) shl 8) or
            ((value and 0xFF0000u) shr 8) or
            ((value and 0xFF000000u) shr 24)