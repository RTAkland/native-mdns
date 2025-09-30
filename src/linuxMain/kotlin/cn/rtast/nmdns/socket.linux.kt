/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/30/25
 */

@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("unused")

package cn.rtast.nmdns

import kotlinx.cinterop.*
import platform.linux.inet_addr
import platform.posix.*

public actual class Socket1 internal actual constructor() {
    /**
     * socket fd
     */
    private var socket: Int = -1

    public actual fun bind(ip: String, port: Int): Socket1 {
        socket = socket(AF_INET, SOCK_DGRAM, 0)
        if (socket < 0) perror("Failed to create udp socket")
        memScoped {
            val reuse = alloc<IntVar>()
            reuse.value = 1
            if (setsockopt(socket, SOL_SOCKET, SO_REUSEADDR, reuse.ptr, sizeOf<IntVar>().convert()) < 0) {
                perror("Failed to setsocketopt SO_REUSEADDR")
            }
        }
        memScoped {
            val addr = alloc<sockaddr_in>()
            addr.sin_family = AF_INET.convert()
            addr.sin_port = htons(port.toUShort())
            addr.sin_addr.s_addr = inet_addr(ip)
            if (bind(socket, addr.ptr.reinterpret(), sizeOf<sockaddr_in>().convert()) < 0) {
                perror("Failed to bind")
                close(socket)
            }
        }
        memScoped {
            val mreq = alloc<ip_mreq>()
            mreq.imr_multiaddr.s_addr = inet_addr("224.0.0.251")
            mreq.imr_interface.s_addr = htonl(INADDR_ANY)
            if (setsockopt(socket, IPPROTO_IP, IP_ADD_MEMBERSHIP, mreq.ptr, sizeOf<ip_mreq>().convert()) < 0) {
                perror("setsockopt IP_ADD_MEMBERSHIP")
            }
        }

        return this
    }

    public actual fun send(packet: ByteArray) {
        memScoped {
            val addr = alloc<sockaddr_in>()
            addr.sin_family = AF_INET.convert()
            addr.sin_port = htons(5353u)
            addr.sin_addr.s_addr = inet_addr("224.0.0.251")
            val cbuf = packet.refTo(0).getPointer(this)
            sendto(socket, cbuf, packet.size.convert(), 0, addr.ptr.reinterpret(), sizeOf<sockaddr_in>().convert())
        }
    }

    public actual fun destroy() {
        close(socket)
    }
}