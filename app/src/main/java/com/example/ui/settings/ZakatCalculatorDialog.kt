package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.AppStrings
import com.example.util.ZakatCalculatorHelper

@Composable
fun ZakatCalculatorDialog(
    onDismiss: () -> Unit
) {
    val isAr = AppStrings.isArabic
    var cashStr by remember { mutableStateOf("") }
    var gold21Str by remember { mutableStateOf("") }
    var gold24Str by remember { mutableStateOf("") }
    var silverStr by remember { mutableStateOf("") }
    var goodsStr by remember { mutableStateOf("") }
    var goldPriceStr by remember { mutableStateOf("60") }

    val cash = cashStr.toDoubleOrNull() ?: 0.0
    val gold21 = gold21Str.toDoubleOrNull() ?: 0.0
    val gold24 = gold24Str.toDoubleOrNull() ?: 0.0
    val silver = silverStr.toDoubleOrNull() ?: 0.0
    val goods = goodsStr.toDoubleOrNull() ?: 0.0
    val goldPrice = goldPriceStr.toDoubleOrNull() ?: 60.0

    val result = remember(cash, gold21, gold24, silver, goods, goldPrice) {
        ZakatCalculatorHelper.calculateZakat(cash, gold24, gold21, silver, goods, goldPrice)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 620.dp).testTag("zakat_calculator_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = if (isAr) "حاسبة الزكاة الشرعية الذكية" else "Smart Zakat Calculator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = if (isAr) "نصاب الزكاة الحالي (85 غرام ذهب): ${String.format("%.1f", result.nisabValue)}" else "Nisab: ${String.format("%.1f", result.nisabValue)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(text = if (isAr) "إجمالي أموالك الزكوية: ${String.format("%.1f", result.totalWealth)}" else "Total Wealth: ${String.format("%.1f", result.totalWealth)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        if (result.isZakatEligible) {
                            Text(text = if (isAr) "مقدار الزكاة الواجبة (2.5%): ${String.format("%.2f", result.zakatAmountDue)}" else "Zakat Due (2.5%): ${String.format("%.2f", result.zakatAmountDue)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text(text = if (isAr) "لم يبلغ المال النصاب (لا تجب فيه الزكاة)" else "Below Nisab threshold", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                OutlinedTextField(value = cashStr, onValueChange = { cashStr = it }, label = { Text(if (isAr) "السيولة والأموال النقدية بالبنك" else "Cash & Bank Balance") }, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = gold24Str, onValueChange = { gold24Str = it }, label = { Text(if (isAr) "ذهب 24 (غرام)" else "24k Gold (g)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = gold21Str, onValueChange = { gold21Str = it }, label = { Text(if (isAr) "ذهب 21 (غرام)" else "21k Gold (g)") }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = silverStr, onValueChange = { silverStr = it }, label = { Text(if (isAr) "فضة (غرام)" else "Silver (g)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = goodsStr, onValueChange = { goodsStr = it }, label = { Text(if (isAr) "بضائع وتجارة" else "Merchandise") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = goldPriceStr, onValueChange = { goldPriceStr = it }, label = { Text(if (isAr) "سعر غرام الذهب عيار 24 لضبط النصاب" else "Gold Price / Gram") }, modifier = Modifier.fillMaxWidth())

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(46.dp)) {
                    Text(if (isAr) "إغلاق" else "Done")
                }
            }
        }
    }
}
