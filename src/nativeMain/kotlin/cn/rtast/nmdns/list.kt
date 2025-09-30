/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/30/25
 */


@file:OptIn(ExperimentalNativeApi::class)

package cn.rtast.nmdns

import kotlin.experimental.ExperimentalNativeApi

/**
 * create an empty list in c
 */
@CExport
@CName("create_txt_records")
public fun createTXTRecords(): List<String> = listOf()

/**
 * add txt record to list
 */
@CExport
@CName("add_txt_record")
public fun addTxtRecord(list: List<String>, value: String) {
    list.toMutableList().add(value)
}