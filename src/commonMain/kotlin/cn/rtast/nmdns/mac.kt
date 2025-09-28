/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/29/25
 */


package cn.rtast.nmdns

private val macAddresses = listOf(
    "ef:42:0d:76:f1:97",
    "b4:91:7c:be:84:7b",
    "38:d0:d3:f5:63:d1",
    "90:d0:d5:3e:5b:fa",
    "4d:84:d4:dd:7e:b4",
    "11:1c:fb:06:6a:4a",
    "cf:97:be:b3:18:5b",
    "0c:34:bf:d8:55:71",
    "95:a0:71:10:31:ab",
    "4c:33:b8:4a:60:8e",
    "fc:89:a5:f2:b6:3c",
    "a7:42:99:34:b5:21",
    "9a:55:3f:5d:bb:9d",
    "2b:35:ce:77:a0:7c",
    "7d:51:21:72:c5:b4",
    "79:7d:cc:e1:30:9b",
    "aa:a2:cf:b5:e3:3c",
    "8d:73:3f:8e:ef:d7",
    "21:3c:a4:56:80:c5",
    "c1:fa:ca:cb:ec:f3",
    "51:76:e2:12:2f:af",
    "a8:03:22:2d:b9:80",
    "2a:84:3b:ad:9c:d9",
    "24:79:c3:ab:0f:b5",
    "bc:44:e3:c4:9e:be",
    "cb:1b:84:c2:94:ba",
    "9d:66:ce:d2:6e:4f",
    "43:bf:e2:b2:e3:78",
    "48:25:03:bb:9d:35",
    "c3:c9:1c:3d:ae:dd"
)

fun randomMacAddress(): String = macAddresses.random()