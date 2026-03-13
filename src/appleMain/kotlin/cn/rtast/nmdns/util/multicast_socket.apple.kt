/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/13
 */

@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.nmdns.util

import kotlinx.cinterop.*
import platform.darwin.inet_addr
import platform.darwin.sockaddr_in
import platform.posix.*

public actual class MulticastSocket actual constructor() {

    private var fd: Int = -1

    public actual fun bind(port: Int): Unit = memScoped {
        fd = socket(AF_INET, SOCK_DGRAM, 0)
        val opt = alloc<IntVar>()
        setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, opt.ptr, sizeOf<IntVar>().convert())
        val addr = alloc<sockaddr_in>()
        addr.sin_family = AF_INET.convert()
        addr.sin_port = htons(port.toUShort())
        addr.sin_addr.s_addr = INADDR_ANY
        bind(fd, addr.ptr.reinterpret(), sizeOf<sockaddr_in>().convert())
    }

    public actual fun joinMulticastGroup(group: String): Unit = memScoped {
        val merq = alloc<ip_mreq>()
        merq.imr_multiaddr.s_addr = inet_addr(group)
        merq.imr_interface.s_addr = INADDR_ANY
        setsockopt(fd, IPPROTO_IP, IP_ADD_MEMBERSHIP, merq.ptr, sizeOf<ip_mreq>().convert())
    }

    public actual fun send(data: ByteArray, host: String, port: Int): Unit = memScoped {
        val addr = alloc<sockaddr_in>()
        addr.sin_family = AF_INET.convert()
        addr.sin_port = htons(port.toUShort())
        addr.sin_addr.s_addr = inet_addr(host)
        data.usePinned {
            sendto(fd, it.addressOf(0), data.size.convert(), 0, addr.ptr.reinterpret(), sizeOf<sockaddr_in>().convert())
        }
    }

    public actual fun receive(buf: ByteArray): Long = memScoped {
        buf.usePinned { recv(fd, it.addressOf(0), buf.size.convert(), 0) }
    }

    public actual fun close(): Unit = run { if (fd > 0) close(fd) }

    public actual fun leaveMulticastGroup(group: String): Unit = memScoped {
        val merq = alloc<ip_mreq>()
        merq.imr_multiaddr.s_addr = inet_addr(group)
        merq.imr_interface.s_addr = INADDR_ANY
        setsockopt(fd, IPPROTO_IP, IP_DROP_MEMBERSHIP, merq.ptr, sizeOf<ip_mreq>().convert())
    }
}