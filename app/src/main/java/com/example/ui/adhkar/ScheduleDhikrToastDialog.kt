package com.example.ui.adhkar

import androidx.compose.runtime.Composable
import com.example.data.TasbeehScheduleRepository
import com.example.model.CustomDhikrList
import com.example.model.DhikrItem
import com.example.model.TasbeehScheduleModel
import com.example.ui.tasbeeh.CreateTasbeehScheduleDialog

@Composable
fun ScheduleDhikrToastDialog(
    item: DhikrItem? = null,
    list: CustomDhikrList? = null,
    onDismiss: () -> Unit,
    onScheduled: (TasbeehScheduleModel) -> Unit = {}
) {
    val initialModel = when {
        item != null -> DhikrToastScheduleHelper.createScheduleFromDhikrItem(item)
        list != null -> DhikrToastScheduleHelper.createScheduleFromDhikrList(list)
        else -> null
    }

    CreateTasbeehScheduleDialog(
        initialItem = initialModel,
        onDismiss = onDismiss,
        onSave = { saved ->
            TasbeehScheduleRepository.addSchedule(saved)
            onScheduled(saved)
            onDismiss()
        }
    )
}
