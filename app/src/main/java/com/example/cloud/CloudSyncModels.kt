package com.example.cloud

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserBackupSnapshot(
    @DocumentId val id: String = "",
    val ownerId: String = "",
    val label: String = "",
    val totalTasbeeh: Long = 0L,
    val totalPagesRead: Int = 0,
    val currentStreak: Int = 0,
    val locksCount: Int = 0,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toWriteMap(): Map<String, Any> = mapOf(
        "ownerId" to ownerId,
        "label" to label.take(80).ifBlank { "Device Backup" },
        "totalTasbeeh" to totalTasbeeh.coerceAtLeast(0L),
        "totalPagesRead" to totalPagesRead.coerceAtLeast(0),
        "currentStreak" to currentStreak.coerceAtLeast(0),
        "locksCount" to locksCount.coerceAtLeast(0),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class KhatmaCircle(
    @DocumentId val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val inviteCode: String = "",
    val targetDays: Int = 30,
    val totalPagesRead: Int = 0,
    val memberUids: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "ownerId" to ownerId,
        "title" to title.take(80).ifBlank { "Khatma Circle" },
        "inviteCode" to inviteCode,
        "targetDays" to targetDays.coerceIn(1, 365),
        "totalPagesRead" to totalPagesRead.coerceAtLeast(0),
        "memberUids" to memberUids.distinct().take(30),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class CircleMember(
    @DocumentId val docId: String = "",
    val uid: String = "",
    val displayName: String = "",
    val pagesRead: Int = 0,
    @ServerTimestamp val joinedAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "displayName" to displayName.take(60).ifBlank { "Member" },
        "pagesRead" to pagesRead.coerceIn(0, 60400),
        "joinedAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}
