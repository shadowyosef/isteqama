package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SmartAlertPreferences
import com.example.model.QuranRiwayah
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayahSelectionSheet(onDismiss: () -> Unit) {
    val selectedRiwayah by SmartAlertPreferences.selectedRiwayah.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(AppStrings.get("اختيار الرواية القرآنية", "Select Quranic Reading (Riwayah)"), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)

            val riwayat = listOf(
                Pair(QuranRiwayah.HAFS_AN_ASIM, "رواية حفص عن عاصم (المصحف المعتمد)"),
                Pair(QuranRiwayah.WARSH_AN_NAFI, "رواية ورش عن نافع من طريق الأزرق")
            )

            riwayat.forEach { (r, label) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedRiwayah == r) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    onClick = {
                        SmartAlertPreferences.selectedRiwayah.value = r
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(label, fontWeight = FontWeight.SemiBold)
                        RadioButton(selected = selectedRiwayah == r, onClick = { SmartAlertPreferences.selectedRiwayah.value = r; onDismiss() })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
