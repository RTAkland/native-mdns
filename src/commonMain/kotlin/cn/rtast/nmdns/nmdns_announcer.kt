/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/28/25
 */


@file:OptIn(ExperimentalNativeApi::class)

package cn.rtast.nmdns

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.CName

public fun NMDNSAnnouncer.onRegister(callback: () -> Unit) {
    onRegisterCallback = callback
}

public fun NMDNSAnnouncer.onBroadcast(callback: () -> Unit) {
    onBroadcastCallback = callback
}

public fun NMDNSAnnouncer.onUnregistered(callback: () -> Unit) {
    onUnregisterCallback = callback
}

public data class NMDNSAnnouncer(
    /**
     * service type
     * airplay service type: _airplay._tcp.local.
     */
    internal val serviceType: String,
    /**
     * service name
     */
    internal val serviceName: String,
    /**
     * local hostname
     */
    internal val hostname: String,
    /**
     * service ip, lan ip mostly
     */
    internal val ipAddress: String,
    /**
     * service port
     */
    internal val port: Int = 7004,
    /**
     * mdns server port
     */
    internal val mdnsPort: Int = 3025,
    /**
     * txt record content
     */
    internal val txtRecords: List<String>,
    /**
     * mdns socket server
     */
    internal val server: Socket1,
    /**
     * srv packet
     */
    internal val packet: ByteArray,
) {

    internal var onRegisterCallback: (() -> Unit)? = null
    internal var onBroadcastCallback: (() -> Unit)? = null
    internal var onUnregisterCallback: (() -> Unit)? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as NMDNSAnnouncer

        if (port != other.port) return false
        if (mdnsPort != other.mdnsPort) return false
        if (serviceType != other.serviceType) return false
        if (serviceName != other.serviceName) return false
        if (hostname != other.hostname) return false
        if (ipAddress != other.ipAddress) return false
        if (txtRecords != other.txtRecords) return false
        if (server != other.server) return false
        if (!packet.contentEquals(other.packet)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = port
        result = 31 * result + mdnsPort
        result = 31 * result + serviceType.hashCode()
        result = 31 * result + serviceName.hashCode()
        result = 31 * result + hostname.hashCode()
        result = 31 * result + ipAddress.hashCode()
        result = 31 * result + txtRecords.hashCode()
        result = 31 * result + server.hashCode()
        result = 31 * result + packet.contentHashCode()
        return result
    }

    /**
     * broadcast packet
     */
    public fun broadcast() {
        this.onBroadcastCallback?.invoke()
        this.server.send(packet)
    }

    /**
     * unregister service
     */
    public fun unregister() {
        this.onUnregisterCallback?.invoke()
        this.server.destroy()
    }
}

/**
 * broadcast packet
 * export for c
 */
@CExport
@CName("broadcast")
public fun broadcast1(server: NMDNSAnnouncer) {
    server.server.send(server.packet)
}

/**
 * unregister service
 * export for c
 */
@CExport
@CName("unregister")
public fun unregister1(server: NMDNSAnnouncer) {
    server.unregister()
}

/**
 * register service
 * airplay2 txt records
 * listOf(
 *     "deviceid=${randomMacAddress()}",
 *     "model=AppleTV3,2C",
 *     "features=0x5A7FFFF7,0x1E",
 *     "srcvers=220.68",
 *     "pk=f3769a660475d27b4f6040381d784645e13e21c53e6d2da6a8c3d757086fc336"
 * )
 */
@CName("register_service")
public fun registerService(
    serviceType: String,
    serviceName: String,
    hostname: String,
    ipAddress: String,
    port: Int,
    mdnsPort: Int = 9872,
    bindAddress: String,
    txtRecords: List<String>,
    callback: NMDNSAnnouncer.() -> Unit = {},
): NMDNSAnnouncer {
    val socket = createSocket()
        .bind(bindAddress, mdnsPort)

    val server = NMDNSAnnouncer(
        serviceType,
        serviceName,
        hostname,
        ipAddress,
        port,
        mdnsPort,
        txtRecords,
        socket,
        buildPacket(serviceType, "$serviceName.$serviceType", hostname, ipAddress, port, txtRecords),
    )
    server.callback()
    server.onRegisterCallback?.invoke()
    return server
}

internal fun buildPacket(
    serviceType: String,
    serviceName: String,
    hostname: String,
    ip: String,
    port: Int,
    txtRecords: List<String>,
): ByteArray {
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

    return header + records.reduce { acc, r -> acc + r }
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