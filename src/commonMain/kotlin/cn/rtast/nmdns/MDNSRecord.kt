/*
 * Copyright © 2026 RTAkland
 * Author: RTAkland
 * Date: 2026/3/14
 */


package cn.rtast.nmdns

public data class MDNSRecord(
    val name: String,
    val type: String,
    val host: String,
    val port: Int,
    val txt: Map<String, String> = emptyMap(),
)