package com.example.ui.circles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cloud.CloudSyncRepository
import com.example.cloud.GoogleAuthHelper
import com.example.cloud.GoogleSignInButton
import com.example.util.AppStrings
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Composable
fun SharedKhatmaCirclesDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val scope = rememberCoroutineScope()
    val user by GoogleAuthHelper.authStateFlow().collectAsState(initial = null)
    val repo = remember { CloudSyncRepository(context) }

    val circles by remember(user?.uid) {
        if (user != null) repo.observeMyCircles() else emptyFlow()
    }.collectAsState(initial = emptyList())

    var newTitle by remember { mutableStateOf("") }
    var targetDays by remember { mutableStateOf("30") }
    var joinCode by remember { mutableStateOf("") }
    var selectedCircleId by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("") }

    val members by remember(user?.uid, selectedCircleId) {
        val cid = selectedCircleId
        if (user != null && cid != null) repo.observeCircleMembers(cid) else emptyFlow()
    }.collectAsState(initial = emptyList())

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(6.dp)) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "حلقات الختمة الجماعية" else "Shared Khatma Circles", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                if (user == null) {
                    Text(if (isAr) "يجب تسجيل الدخول عبر Google للمشاركة في الحلقات." else "Google Sign-In required for shared circles.", fontSize = 12.sp)
                    GoogleSignInButton(onError = { feedback = it })
                } else {
                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text(if (isAr) "اسم الحلقة (مثلاً: ختمة العائلة)" else "Circle Name") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = targetDays, onValueChange = { targetDays = it.filter { c -> c.isDigit() } }, label = { Text(if (isAr) "الأيام" else "Days") }, modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) scope.launch {
                                    val res = repo.createCircle(newTitle, targetDays.toIntOrNull() ?: 30)
                                    feedback = res.fold(
                                        onSuccess = { selectedCircleId = it.inviteCode; newTitle = ""; if (isAr) "تم إنشاء الحلقة: ${it.inviteCode}" else "Created: ${it.inviteCode}" },
                                        onFailure = { it.message ?: "Error" }
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f).heightIn(min = 52.dp)
                        ) { Text(if (isAr) "إنشاء" else "Create") }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = joinCode, onValueChange = { joinCode = it }, label = { Text(if (isAr) "كود الدعوة" else "Invite Code") }, modifier = Modifier.weight(1f))
                        FilledTonalButton(
                            onClick = {
                                if (joinCode.isNotBlank()) scope.launch {
                                    val res = repo.joinCircleByCode(joinCode)
                                    feedback = res.fold(
                                        onSuccess = { selectedCircleId = it.inviteCode; joinCode = ""; if (isAr) "تم الانضمام بنجاح!" else "Joined!" },
                                        onFailure = { it.message ?: "Not found" }
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f).heightIn(min = 52.dp)
                        ) { Text(if (isAr) "انضمام" else "Join") }
                    }

                    HorizontalDivider()
                    circles.forEach { circle ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedCircleId == circle.inviteCode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("${circle.title} (${circle.inviteCode})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${circle.memberUids.size} ${if (isAr) "أعضاء" else "members"} • ${circle.totalPagesRead} ${if (isAr) "صفحة" else "pages"}", fontSize = 11.sp)
                                }
                                TextButton(onClick = { selectedCircleId = circle.inviteCode }) { Text(if (isAr) "عرض" else "Open") }
                            }
                        }
                    }

                    if (selectedCircleId != null) {
                        Button(
                            onClick = { scope.launch { repo.addCircleMemberPages(selectedCircleId!!, 5) } },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (isAr) "تسجيل +٥ صفحات في هذه الحلقة" else "Log +5 Pages to Circle") }
                        members.forEach { m ->
                            Text("• ${m.displayName}: ${m.pagesRead} ${if (isAr) "صفحة" else "pages"}", fontSize = 12.sp)
                        }
                    }
                }
                if (feedback.isNotEmpty()) Text(feedback, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
