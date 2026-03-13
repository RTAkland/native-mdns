/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/13
 */


package cn.rtast.nmdns.util

public expect class MulticastSocket internal constructor() {
    /**
     * bind to multicast addr and port
     */
    public fun bind(port: Int)

    /**
     * join multicast group
     */
    public fun joinMulticastGroup(group: String)

    /**
     * leave multicast group
     */
    public fun leaveMulticastGroup(group: String)

    /**
     * send binary packet
     */
    public fun send(data: ByteArray, host: String, port: Int)

    /**
     * receive packet from multicast group
     */
    public fun receive(buf: ByteArray): Long

    /**
     * close socket
     */
    public fun close()
}