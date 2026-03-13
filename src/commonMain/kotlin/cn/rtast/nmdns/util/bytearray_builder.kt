/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/13
 */


package cn.rtast.nmdns.util

internal class ByteArrayBuilder {
    private val buffer = mutableListOf<Byte>()

    fun appendByte(b: Int) = apply { buffer.add(b.toByte()) }

    fun appendShort(value: Int) = apply {
        buffer.add(((value shr 8) and 0xFF).toByte())
        buffer.add((value and 0xFF).toByte())
    }

    fun appendInt(value: Int) = apply {
        buffer.add(((value shr 24) and 0xFF).toByte())
        buffer.add(((value shr 16) and 0xFF).toByte())
        buffer.add(((value shr 8) and 0xFF).toByte())
        buffer.add((value and 0xFF).toByte())
    }

    fun appendBytes(bytes: ByteArray) = apply { buffer.addAll(bytes.toList()) }

    fun appendName(name: String): ByteArrayBuilder {
        val labels = name.split(".")
        for (label in labels) {
            appendByte(label.length)
            appendBytes(label.encodeToByteArray())
        }
        appendByte(0)
        return this
    }

    fun build(): ByteArray = buffer.toByteArray()
}

internal fun dnsPacket(block: ByteArrayBuilder.() -> Unit) =
    ByteArrayBuilder().apply(block).build()