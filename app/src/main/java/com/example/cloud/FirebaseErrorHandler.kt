package com.example.cloud

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

enum class OperationType { CREATE, UPDATE, DELETE, LIST, GET, WRITE }

data class FirestoreErrorInfo(
    val error: String?,
    val operationType: OperationType,
    val path: String?,
    val userId: String?
)

fun handleFirestoreError(
    error: Exception,
    operationType: OperationType,
    path: String?
): FirestoreErrorInfo {
    val auth = Firebase.auth
    val errInfo = FirestoreErrorInfo(
        error = error.message ?: "Unknown Firestore error",
        operationType = operationType,
        path = path,
        userId = auth.currentUser?.uid
    )
    Log.e("FirestoreError", "[$operationType @ $path] user=${errInfo.userId}: ${errInfo.error}")
    return errInfo
}
