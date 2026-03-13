/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/13
 */


@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.nmdns.util

import kotlinx.cinterop.*
import platform.posix.*
import platform.windows.AF_INET
import platform.windows.IPPROTO_UDP
import platform.windows.SOCK_DGRAM
import platform.windows.WSAStartup
import platform.windows.setsockopt

public actual class MulticastSocket actual constructor() {

    private var fd: SOCKET = INVALID_SOCKET

    public actual fun bind(port: Int): Unit = memScoped {
        val wsaData = alloc<WSADATA>()
        if (WSAStartup(0x202u, wsaData.ptr) != 0) perror("Failed to create socket")
        fd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)
        setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, "1", sizeOf<IntVar>().convert())
        val addr = alloc<SOCKADDR_IN>()
        addr.sin_family = AF_INET.convert()
        addr.sin_port = htons(port.toUShort())
        addr.sin_addr.S_un.S_addr = INADDR_ANY
        bind(fd, addr.ptr.reinterpret(), sizeOf<SOCKADDR_IN>().convert())
    }

    public actual fun joinMulticastGroup(group: String): Unit = memScoped {
        // ref: clibs/win32_join_multicast.c:7
        win32_join_multicast_group.join_multicast_group(fd, group)
    }

    public actual fun send(data: ByteArray, host: String, port: Int): Unit = memScoped {
        val addr = alloc<SOCKADDR_IN>()
        addr.sin_family = AF_INET.convert()
        addr.sin_port = htons(port.toUShort())
        addr.sin_addr.S_un.S_addr = inet_addr(host)
        data.usePinned {
            // ref: clibs/win32_join_multicast.c:18
            win32_join_multicast_group.socket_sendto(
                fd, it.addressOf(0).reinterpret(),
                data.size, host, port.toUShort()
            )
        }
    }

    public actual fun receive(buf: ByteArray): Long = memScoped {
        buf.usePinned { recv(fd, it.addressOf(0), buf.size.convert(), 0) }.toLong()
    }

    public actual fun close() {
        closesocket(fd)
        WSACleanup()
    }

    public actual fun leaveMulticastGroup(group: String) {
        // ref: clibs/win32_join_multicast.c:31
        win32_join_multicast_group.leave_multicast_group(fd, group)
    }
}