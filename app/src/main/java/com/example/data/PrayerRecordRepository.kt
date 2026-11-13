package com.example.data

import android.content.Context
import com.example.model.*
import java.util.Calendar

object PrayerRecordRepository {

    fun isFuturePrayerToday(item: PrayerTimeItem): Boolean {
        if (item.isPassed) return false
        val now = Calendar.getInstance()
        val nowMins = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val prayerMins = item.hour * 60 + item.minute
        return nowMins < prayerMins
    }

    fun canMarkPrayer(item: PrayerTimeItem, dateKey: String = DailyProgressRepository.todayKey()): Pair<Boolean, String> {
        val today = DailyProgressRepository.todayKey()
        if (dateKey != today) {
            // Past dates can always be marked or updated
            return true to ""
        }
        if (isFuturePrayerToday(item)) {
            val msgAr = "لم يحن موعد صلاة ${item.type.nameAr} بعد اليوم. لا يمكن تأكيد صلاة مستقبلية."
            val msgEn = "${item.type.nameEn} prayer has not arrived yet today. Cannot mark future prayers."
            return false to if (com.example.util.AppStrings.isArabic) msgAr else msgEn
        }
        return true to ""
    }

    fun markOrUpdatePrayer(context: Context, record: PrayerDailyRecord) {
        DailyProgressRepository.recordPrayer(context, record)
        if (record.dateKey == DailyProgressRepository.todayKey()) {
            PrayerTimesRepository.togglePrayerPrayed(context, record.prayerType ?: return, record.isPrayed)
        }
    }

    fun deletePrayerMark(context: Context, dateKey: String, prayerType: PrayerType) {
        val record = PrayerDailyRecord(
            dateKey = dateKey,
            prayerId = prayerType.id,
            isPrayed = false,
            status = PrayerStatus.NOT_PRAYED
        )
        DailyProgressRepository.recordPrayer(context, record)
        if (dateKey == DailyProgressRepository.todayKey()) {
            PrayerTimesRepository.togglePrayerPrayed(context, prayerType, false)
        }
    }

    fun getRecord(dateKey: String, prayerId: String): PrayerDailyRecord? {
        return DailyProgressRepository.getRecord(dateKey).prayersMarked[prayerId]
    }
}
