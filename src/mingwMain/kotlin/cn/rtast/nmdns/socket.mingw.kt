/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/30/25
 */

@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("unused")

package cn.rtast.nmdns

import kotlinx.cinterop.*
import platform.posix.*
import platform.windows.IPPROTO_UDP
import platform.windows.SOCK_DGRAM
import platform.windows.WSAStartup
import platform.windows.inet_addr
import platform.windows.setsockopt

public actual class Socket1 internal actual constructor() {

    /**
     * socket fd in windows
     */
    private var sock: SOCKET = INVALID_SOCKET
    private var bound: Boolean = false

    public actual fun bind(ip: String, port: Int): Socket1 = memScoped {
        val wsaData = nativeHeap.alloc<WSADATA>()
        if (WSAStartup(0x202u, wsaData.ptr) != 0) {
            perror("Failed to create socket")
        }
        sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)
        if (sock == INVALID_SOCKET) {
            perror("Failed to create socket")
            WSACleanup()
        }

        val result = setsockopt(
            sock,
            SOL_SOCKET,
            SO_REUSEADDR,
            "1",
            sizeOf<IntVar>().convert()
        )
        if (result != 0) println("setsockopt failed")

        val addr = alloc<SOCKADDR_IN>()
        addr.sin_family = AF_INET.convert()
        addr.sin_port = htons(port.toUShort())
        addr.sin_addr.S_un.S_addr = inet_addr(ip)

        if (bind(sock, addr.ptr.reinterpret(), sizeOf<SOCKADDR_IN>().convert()) == SOCKET_ERROR) {
            println("Bind failed")
            closesocket(sock)
            WSACleanup()
        }

        bound = true
        this@Socket1
    }

    public actual fun send(packet: ByteArray): Unit = memScoped {
        if (!bound) throw IllegalStateException("Socket not bound")
        val dest = alloc<SOCKADDR_IN>()
        dest.sin_family = AF_INET.convert()
        dest.sin_port = htons(5353u)
        dest.sin_addr.S_un.S_addr = inet_addr("224.0.0.251")
        packet.usePinned { pinned ->
            val sent = sendto(
                sock, pinned.addressOf(0),
                packet.size.convert(),
                0, dest.ptr.reinterpret(),
                sizeOf<SOCKADDR_IN>().convert()
            )
            if (sent == SOCKET_ERROR) perror("Send failed")
        }
    }

    public actual fun destroy() {
        if (sock != INVALID_SOCKET) {
            closesocket(sock)
            sock = INVALID_SOCKET
        }
        WSACleanup()
    }
}