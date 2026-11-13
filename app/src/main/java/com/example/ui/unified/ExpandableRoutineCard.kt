package com.example.ui.unified

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UnifiedRoutineRepository
import com.example.model.UnifiedRoutine
import com.example.util.AppStrings

@Composable
fun ExpandableRoutineCard(
    routine: UnifiedRoutine,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val steps = remember(routine.stepsJson) { routine.getSteps() }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (AppStrings.isArabic) routine.titleAr else routine.titleEn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${routine.currentCount} من ${routine.targetCount} منجز • ${steps.size} أذكار متسلسلة",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (steps.size > 1) {
                        Text(
                            text = "سلسلة الروتين: " + steps.joinToString(" ➔ ") { "${it.textAr} (${it.targetCount})" },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 16.sp
                        )
                    }
                    Text(text = routine.detailsSummary, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(onClick = { ShareRoutineHelper.share(context, routine) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp), modifier = Modifier.height(32.dp)) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مشاركة", fontSize = 11.sp)
                            }
                            OutlinedButton(onClick = { UnifiedRoutineRepository.duplicateRoutine(routine.id) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp), modifier = Modifier.height(32.dp)) {
                                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("نسخ", fontSize = 11.sp)
                            }
                        }
                        Button(onClick = { UnifiedRoutineRepository.incrementCount(routine.id) }, modifier = Modifier.height(32.dp)) {
                            Text("+1", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
