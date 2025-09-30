/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/30/25
 */


package cn.rtast.nmdns

import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket

public actual class Socket1 internal actual constructor() {
    private lateinit var socket: MulticastSocket
    private val group: InetAddress = InetAddress.getByName("224.0.0.251")

    public actual fun bind(ip: String, port: Int): Socket1 {
        socket = MulticastSocket(InetSocketAddress(ip, port))
        socket.joinGroup(group)
        return this
    }

    public actual fun send(packet: ByteArray) {
        socket.send(DatagramPacket(packet, packet.size, group, 5353))
    }

    public actual fun destroy() {
        socket.close()
    }
}