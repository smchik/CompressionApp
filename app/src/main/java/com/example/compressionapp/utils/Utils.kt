package com.example.compressionapp.utils

fun Int.formatByteSize(): String = toLong().formatByteSize()

fun Long.formatByteSize(): String {
    val kb = this / 1024.0
    val mb = kb / 1024.0

    return when {
        mb >= 1 -> String.format("%.2f MB", mb)  // If size is in MB
        kb >= 1 -> String.format("%.2f KB", kb)  // If size is in KB
        else -> String.format("%d Bytes", this)  // If size is in Bytes
    }
}

