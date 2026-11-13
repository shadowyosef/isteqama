package com.example.ui.unified

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.data.UnifiedRoutineRepository
import com.example.model.UnifiedRoutine

@Composable
fun RoutineActionMenu(
    expanded: Boolean,
    routine: UnifiedRoutine,
    context: Context,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text("تجربة القفل الآن (Test Lock)") },
            leadingIcon = { Icon(Icons.Default.LockOpen, null, tint = MaterialTheme.colorScheme.primary) },
            onClick = {
                onDismiss()
                com.example.ui.lock.ActiveLockActivity.launch(context, routine.toScreenLockEntity())
            }
        )
        DropdownMenuItem(
            text = { Text("استنساخ (Duplicate)") },
            leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
            onClick = {
                onDismiss()
                UnifiedRoutineRepository.duplicateRoutine(routine.id)
            }
        )
        DropdownMenuItem(
            text = { Text("مشاركة (Share)") },
            leadingIcon = { Icon(Icons.Default.Share, null) },
            onClick = {
                onDismiss()
                ShareRoutineHelper.share(context, routine)
            }
        )
        DropdownMenuItem(
            text = { Text("تعديل (Edit)") },
            leadingIcon = { Icon(Icons.Default.Edit, null) },
            onClick = {
                onDismiss()
                onEdit()
            }
        )
        DropdownMenuItem(
            text = { Text(if (routine.isLockActive) "إلغاء قفل الشاشة" else "تفعيل قفل الشاشة") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            onClick = {
                onDismiss()
                UnifiedRoutineRepository.toggleLock(routine.id)
            }
        )
        DropdownMenuItem(
            text = { Text("حذف (Delete)", color = MaterialTheme.colorScheme.error) },
            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
            onClick = {
                onDismiss()
                UnifiedRoutineRepository.deleteRoutine(routine.id)
            }
        )
    }
}
