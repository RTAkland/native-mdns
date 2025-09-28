/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/28/25
 */


package cn.rtast.nmdns

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.io.Buffer

data class NMDNSServer(
    /**
     * coroutine dispatcher for running server
     */
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    /**
     * service type
     * default airplay
     */
    val serviceType: String = "_airplay._tcp.local.",
    /**
     * service name
     * finally: $serviceName.$serviceType
     */
    val serviceName: String = "APS",
    /**
     * local hostname
     */
    val hostname: String,
    /**
     * service ip, lan ip mostly
     */
    val ipAddress: String,
    /**
     * service port
     */
    val port: Int = 7004,
    /**
     * mdns server port
     */
    val mdnsPort: Int = 3025,
    /**
     * Txt record content
     */
    val txtRecords: List<String> = listOf(
        "deviceid=01:23:45:67:89:AB",
        "model=AppleTV3,2C",
        "features=0x5A7FFFF7,0x1E",
        "srcvers=220.68",
        "pk=f3769a660475d27b4f6040381d784645e13e21c53e6d2da6a8c3d757086fc336"
    ),
    /**
     * mdns socket server
     */
    val server: BoundDatagramSocket,
    /**
     * srv packet
     */
    val packet: Buffer,
)

suspend fun createService(
    serviceType: String,
    serviceName: String,
    hostname: String,
    ipAddress: String,
    port: Int,
    mdnsPort: Int = 9872,
    bindAddress: String,
    txtRecords: List<String> = listOf(
        "deviceid=${randomMacAddress()}",
        "model=AppleTV3,2C",
        "features=0x5A7FFFF7,0x1E",
        "srcvers=220.68",
        "pk=f3769a660475d27b4f6040381d784645e13e21c53e6d2da6a8c3d757086fc336"
    ),
): NMDNSServer {
    val manager = SelectorManager(Dispatchers.Default)
    val serverSocket = aSocket(manager).udp()
        .bind(InetSocketAddress(bindAddress, mdnsPort))
    return NMDNSServer(
        Dispatchers.Default, serviceType, serviceName,
        hostname, ipAddress, port, mdnsPort,
        txtRecords, serverSocket, buildPacket(serviceType, "$serviceName.$serviceType", hostname, ipAddress, port, txtRecords)
    )
}

suspend fun NMDNSServer.broadcast() {
    this.server.send(Datagram(packet.copy(), InetSocketAddress("224.0.0.251", 5353)))
}

internal fun buildPacket(
    serviceType: String,
    serviceName: String,
    hostname: String,
    ip: String,
    port: Int,
    txtRecords: List<String>,
): Buffer {
    val records = mutableListOf<ByteArray>()
    records += encodeRR(serviceType, 12, encodeName(serviceName))

    val srvData = ByteArray(6) { 0 }
    srvData[4] = (port shr 8).toByte()
    srvData[5] = (port and 0xFF).toByte()
    records += encodeRR(serviceName, 33, srvData + encodeName(hostname))

    val ipParts = ip.split(".").map { it.toInt().toByte() }
    records += encodeRR(hostname, 1, ipParts.toByteArray())

    if (txtRecords.isNotEmpty()) {
        val txtData = encodeTxt(txtRecords)
        records += encodeRR(serviceName, 16, txtData)
    }

    val header = ByteArray(12)
    header[2] = 0x84.toByte()
    header[3] = 0x00

    header[6] = ((records.size shr 8) and 0xFF).toByte()
    header[7] = (records.size and 0xFF).toByte()

    val buffer = Buffer().apply { write(header + records.reduce { acc, r -> acc + r }) }
    return buffer
}

internal fun encodeRR(name: String, type: Int, data: ByteArray): ByteArray {
    val nameBytes = encodeName(name)
    val rr = ByteArray(10)
    rr[0] = 0x00; rr[1] = type.toByte()
    rr[2] = 0x00; rr[3] = 0x01
    rr[4] = 0x00; rr[5] = 0x00; rr[6] = 0x00; rr[7] = 0x78
    rr[8] = (data.size shr 8).toByte()
    rr[9] = (data.size and 0xFF).toByte()
    return nameBytes + rr + data
}

internal fun encodeName(name: String): ByteArray {
    val parts = name.trimEnd('.').split(".")
    val bytes = mutableListOf<Byte>()
    for (p in parts) {
        bytes += p.length.toByte()
        bytes += p.encodeToByteArray().toList()
    }
    bytes += 0
    return bytes.toByteArray()
}

internal fun encodeTxt(records: List<String>): ByteArray {
    val bytes = mutableListOf<Byte>()
    for (txt in records) {
        val txtBytes = txt.encodeToByteArray()
        if (txtBytes.size > 255) continue
        bytes += txtBytes.size.toByte()
        bytes += txtBytes.toList()
    }
    return bytes.toByteArray()
}