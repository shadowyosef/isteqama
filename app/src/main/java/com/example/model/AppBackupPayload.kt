package com.example.model

data class AppBackupMetadata(
    val fileName: String,
    val filePath: String,
    val timestampMillis: Long,
    val formattedDate: String,
    val sizeBytes: Long,
    val recordsCount: Int
)
