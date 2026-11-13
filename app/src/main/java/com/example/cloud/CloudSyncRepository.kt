package com.example.cloud

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CloudSyncRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id)),
        Firebase.auth
    )

    private fun currentUid(): String? = auth.currentUser?.uid
    private fun currentName(): String = auth.currentUser?.displayName?.take(60)?.ifBlank { "Believer" } ?: "Believer"

    suspend fun uploadBackupSnapshot(snapshot: UserBackupSnapshot): Result<Unit> {
        val uid = currentUid() ?: return Result.failure(IllegalStateException("Auth required"))
        val path = "users/$uid/backups/latest_device_sync"
        return try {
            db.document(path).set(snapshot.copy(ownerId = uid).toWriteMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeMyBackups(): Flow<List<UserBackupSnapshot>> = callbackFlow {
        val uid = currentUid()
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }
        val reg = db.collection("users").document(uid).collection("backups")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { handleFirestoreError(err, OperationType.LIST, "users/$uid/backups"); return@addSnapshotListener }
                trySend(snap?.toObjects(UserBackupSnapshot::class.java) ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    suspend fun createCircle(title: String, targetDays: Int): Result<KhatmaCircle> {
        val uid = currentUid() ?: return Result.failure(IllegalStateException("Auth required"))
        val code = UUID.randomUUID().toString().replace("-", "").take(6).uppercase()
        val circle = KhatmaCircle(id = code, ownerId = uid, title = title.trim(), inviteCode = code, targetDays = targetDays.coerceIn(1, 365), memberUids = listOf(uid))
        return try {
            db.collection("circles").document(code).set(circle.toCreateMap()).await()
            val member = CircleMember(uid = uid, displayName = currentName(), pagesRead = 0)
            db.collection("circles").document(code).collection("members").document(uid).set(member.toCreateMap()).await()
            Result.success(circle)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "circles/$code")
            Result.failure(e)
        }
    }

    suspend fun joinCircleByCode(rawCode: String): Result<KhatmaCircle> {
        val uid = currentUid() ?: return Result.failure(IllegalStateException("Auth required"))
        val code = rawCode.trim().uppercase()
        if (!Regex("^[A-Z0-9_-]{3,64}$").matches(code)) return Result.failure(IllegalArgumentException("Invalid invite code"))
        return try {
            val circleRef = db.collection("circles").document(code)
            val circle = circleRef.get().await().toObject(KhatmaCircle::class.java) ?: return Result.failure(NoSuchElementException("Circle not found"))
            if (uid !in circle.memberUids) {
                circleRef.update(mapOf("memberUids" to (circle.memberUids + uid).distinct().take(30), "updatedAt" to FieldValue.serverTimestamp())).await()
            }
            val memRef = circleRef.collection("members").document(uid)
            if (!memRef.get().await().exists()) {
                memRef.set(CircleMember(uid = uid, displayName = currentName(), pagesRead = 0).toCreateMap()).await()
            }
            Result.success(circle)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "circles/$code")
            Result.failure(e)
        }
    }

    fun observeMyCircles(): Flow<List<KhatmaCircle>> = callbackFlow {
        val uid = currentUid()
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }
        val reg = db.collection("circles").whereArrayContains("memberUids", uid).addSnapshotListener { snap, err ->
            if (err != null) { handleFirestoreError(err, OperationType.LIST, "circles"); return@addSnapshotListener }
            trySend(snap?.toObjects(KhatmaCircle::class.java) ?: emptyList())
        }
        awaitClose { reg.remove() }
    }

    fun observeCircleMembers(circleId: String): Flow<List<CircleMember>> = callbackFlow {
        if (currentUid() == null) { trySend(emptyList()); close(); return@callbackFlow }
        val reg = db.collection("circles").document(circleId).collection("members").addSnapshotListener { snap, err ->
            if (err != null) { handleFirestoreError(err, OperationType.LIST, "circles/$circleId/members"); return@addSnapshotListener }
            trySend(snap?.toObjects(CircleMember::class.java) ?: emptyList())
        }
        awaitClose { reg.remove() }
    }

    suspend fun addCircleMemberPages(circleId: String, deltaPages: Int): Result<Unit> {
        val uid = currentUid() ?: return Result.failure(IllegalStateException("Auth required"))
        if (deltaPages <= 0) return Result.success(Unit)
        return try {
            val circleRef = db.collection("circles").document(circleId)
            val memRef = circleRef.collection("members").document(uid)
            val curMem = memRef.get().await().toObject(CircleMember::class.java)
            memRef.update(mapOf("pagesRead" to ((curMem?.pagesRead ?: 0) + deltaPages).coerceAtMost(60400), "updatedAt" to FieldValue.serverTimestamp())).await()
            val curCircle = circleRef.get().await().toObject(KhatmaCircle::class.java)
            circleRef.update(mapOf("totalPagesRead" to ((curCircle?.totalPagesRead ?: 0) + deltaPages).coerceAtLeast(0), "updatedAt" to FieldValue.serverTimestamp())).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "circles/$circleId/members/$uid")
            Result.failure(e)
        }
    }
}
