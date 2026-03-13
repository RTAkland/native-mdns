/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/14
 */


package cn.rtast.nmdns

import cn.rtast.nmdns.util.MulticastSocket

public class MDNSAnnouncer(private val socket: MulticastSocket) {

    public fun register(record: MDNSRecord) {

    }

    public fun unregister(record: MDNSRecord) {

    }

    public fun onQuery(callback: (queryType: String, queryName: String) -> Unit) {}

    public fun start() {

    }


    public fun stop() {

    }
}