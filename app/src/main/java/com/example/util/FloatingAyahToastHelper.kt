package com.example.util

import android.content.Context
import com.example.model.TasbeehCardShape
import com.example.model.TasbeehDeliveryStyle
import com.example.model.TasbeehScheduleModel
import com.example.model.TasbeehThemeColor
import com.example.service.FloatingTasbeehToastManager

object FloatingAyahToastHelper {
    private const val PREFS = "hourly_ayah_toast_prefs"
    private const val KEY_ENABLED = "hourly_ayah_enabled"
    private const val KEY_LAST_HOUR = "last_triggered_hour_key"

    private val shortAyahs = listOf(
        "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ" to "سورة الرعد • آية ٢٨",
        "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا" to "سورة الشرح • آية ٥",
        "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ" to "سورة الطلاق • آية ٣",
        "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي" to "سورة البقرة • آية ١٥٢",
        "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي" to "سورة طه • آية ٢٥",
        "وَقُل رَّبِّ زِدْنِي عِلْمًا" to "سورة طه • آية ١١٤",
        "إِنَّ اللَّهَ مَعَ الصَّابِرِينَ" to "سورة البقرة • آية ١٥٣",
        "وَهُوَ مَعَكُمْ أَيْنَ مَا كُنتُمْ" to "سورة الحديد • آية ٤"
    )

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun checkHourlyTrigger(context: Context, hour: Int, minute: Int, dayOfYear: Int) {
        if (!isEnabled(context) || minute != 0) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val key = "${dayOfYear}_$hour"
        if (prefs.getString(KEY_LAST_HOUR, "") == key) return
        prefs.edit().putString(KEY_LAST_HOUR, key).apply()
        triggerNow(context, hour)
    }

    fun triggerNow(context: Context, seed: Int = (System.currentTimeMillis() / 1000L).toInt()) {
        val (ayahText, ref) = shortAyahs[kotlin.math.abs(seed) % shortAyahs.size]
        val toastModel = TasbeehScheduleModel(
            id = "hourly_ayah_toast",
            phraseAr = "﴿ $ayahText ﴾",
            phraseEn = ref,
            targetCount = 1,
            durationSeconds = 12,
            deliveryStyle = TasbeehDeliveryStyle.TOAST_POPUP,
            themeColor = TasbeehThemeColor.GOLD,
            cardShape = TasbeehCardShape.ROUNDED,
            countOnClickWithDismiss = false
        )
        FloatingTasbeehToastManager.triggerTasbeehAlert(context, toastModel)
    }
}
