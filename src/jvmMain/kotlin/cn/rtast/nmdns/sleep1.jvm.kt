/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/29/25
 */

package cn.rtast.nmdns

actual fun sleep1(time: Int) {
    Thread.sleep(time * 1000L)
}