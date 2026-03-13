/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/13
 */


package cn.rtast.nmdns.util

internal fun buildMDNSResponse(
    host: String,
    service: String,
    ip: String,
    port: Int,
    txtRecords: Map<String, String>,
): ByteArray = dnsPacket {
    // HEADER
    appendShort(0); appendShort(0x8400)
    appendShort(0); appendShort(4)
    appendShort(0); appendShort(0)

    // PTR
    appendName("_airplay._tcp.local"); appendShort(12)
    appendShort(0x8001); appendInt(4500)
    val ptr = dnsPacket ptr@{ this@ptr.appendName(service) }
    appendShort(ptr.size); appendBytes(ptr)

    // SRV
    appendName(service); appendShort(33)
    appendShort(0x8001); appendInt(120);
    appendShort(0); appendShort(0)
    appendShort(port)
    val srv = dnsPacket srv@{ this@srv.appendName(host) }
    appendShort(srv.size); appendBytes(srv)

    // TXT
    appendName(service); appendShort(16)
    appendShort(0x8001); appendInt(4500)
    val txt = dnsPacket txt@{
        for ((k, v) in txtRecords) {
            val entry = "$k=$v".encodeToByteArray()
            this@txt.appendByte(entry.size); this@txt.appendBytes(entry)
        }
    }
    appendShort(txt.size); appendBytes(txt)

    // A
    appendName(host); appendShort(1)
    appendShort(0x8001); appendInt(120)
    val ipParts = ip.split(".").map { it.toInt() and 0xff }
    appendShort(4); appendByte(ipParts[0]); appendByte(ipParts[1])
    appendByte(ipParts[2]); appendByte(ipParts[3])
}
