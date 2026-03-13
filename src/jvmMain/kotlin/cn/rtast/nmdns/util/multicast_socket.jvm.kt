/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/13
 */


package cn.rtast.nmdns.util

import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.MulticastSocket as JvmMulticastSocket

public actual class MulticastSocket actual constructor() {

    private var socket: JvmMulticastSocket? = null

    public actual fun bind(port: Int) {
        socket = JvmMulticastSocket(port)
        socket?.reuseAddress = true
    }

    public actual fun joinMulticastGroup(group: String) {
        val groupAddr = InetAddress.getByName(group)
        val ni = NetworkInterface.getByName(null)
        socket?.joinGroup(InetSocketAddress(groupAddr, 0), ni)
    }

    public actual fun send(data: ByteArray, host: String, port: Int) {
        val addr = InetAddress.getByName(host)
        val packet = DatagramPacket(data, data.size, addr, port)
        socket?.send(packet)
    }

    public actual fun receive(buf: ByteArray): Long {
        val packet = DatagramPacket(buf, buf.size)
        socket?.receive(packet)
        return packet.length.toLong()
    }

    public actual fun close() {
        socket?.close()
        socket = null
    }

    public actual fun leaveMulticastGroup(group: String) {
        val groupAddr = InetAddress.getByName(group)
        val ni = NetworkInterface.getByName(null)
        socket?.leaveGroup(InetSocketAddress(groupAddr, 0), ni)
    }
}