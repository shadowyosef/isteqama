package com.example.util

import com.example.model.KhatmaDistributionMode
import com.example.model.KhatmaLockPlan
import com.example.model.LockScheduleSlot
import kotlin.math.ceil

object KhatmaScheduleCalculator {
    const val QURAN_TOTAL_PAGES = 604

    fun calculatePlan(
        targetDays: Int,
        startHour: Int,
        endHour: Int,
        mode: KhatmaDistributionMode,
        intervalMinutes: Int = 60,
        customSessions: Int = 5,
        quietEnabled: Boolean = false,
        quietStart: Int = 0,
        quietEnd: Int = 7
    ): KhatmaLockPlan {
        val safeDays = targetDays.coerceIn(1, 365)
        val pagesPerDay = ceil(QURAN_TOTAL_PAGES.toDouble() / safeDays.toDouble()).toInt()
        val activeHours = ((endHour - startHour + 24) % 24).coerceAtLeast(1)
        val activeTotalMinutes = activeHours * 60

        val rawTimes = mutableListOf<Pair<Int, Int>>()
        when (mode) {
            KhatmaDistributionMode.INTERVAL_HOURLY -> {
                val step = intervalMinutes.coerceIn(30, 240)
                var offset = 0
                while (offset <= activeTotalMinutes) {
                    val totalMin = (startHour * 60 + offset) % 1440
                    rawTimes.add(totalMin / 60 to totalMin % 60)
                    offset += step
                }
            }
            KhatmaDistributionMode.PRAYER_TIMES -> {
                rawTimes.addAll(listOf(5 to 15, 12 to 45, 15 to 45, 18 to 30, 20 to 0))
            }
            KhatmaDistributionMode.CUSTOM_SESSIONS -> {
                val safeSessions = customSessions.coerceIn(1, 20)
                val step = if (safeSessions > 1) activeTotalMinutes / (safeSessions - 1) else activeTotalMinutes
                for (i in 0 until safeSessions) {
                    val totalMin = (startHour * 60 + (i * step)) % 1440
                    rawTimes.add(totalMin / 60 to totalMin % 60)
                }
            }
        }

        val times = rawTimes.filterNot { (h, _) ->
            LockScheduleTracker.isHourInRestWindow(h, quietEnabled, quietStart, quietEnd)
        }.ifEmpty { rawTimes.take(1) }

        val sessionCount = times.size.coerceAtLeast(1)
        val pagesPerSession = ceil(pagesPerDay.toDouble() / sessionCount.toDouble()).toInt().coerceAtLeast(1)

        return KhatmaLockPlan(
            targetDays = safeDays,
            startHour = startHour,
            endHour = endHour,
            distributionMode = mode,
            intervalMinutes = intervalMinutes,
            customSessionsCount = customSessions,
            totalPages = QURAN_TOTAL_PAGES,
            pagesPerDay = pagesPerDay,
            pagesPerSession = pagesPerSession,
            scheduleTimes = times
        )
    }

    fun buildScheduleSlots(
        plan: KhatmaLockPlan,
        maxDailyRepeats: Int = 0,
        quietEnabled: Boolean = false,
        quietStart: Int = 0,
        quietEnd: Int = 7
    ): List<LockScheduleSlot> {
        val allDays = listOf(1, 2, 3, 4, 5, 6, 7)
        return plan.scheduleTimes.mapIndexed { idx, (h, m) ->
            LockScheduleSlot(
                id = "khatma_slot_${idx + 1}",
                hour = h,
                minute = m,
                daysOfWeek = allDays,
                maxDailyRepeats = maxDailyRepeats,
                quietHoursEnabled = quietEnabled,
                quietHoursStart = quietStart,
                quietHoursEnd = quietEnd
            )
        }.ifEmpty {
            listOf(
                LockScheduleSlot(
                    hour = plan.startHour,
                    minute = 0,
                    maxDailyRepeats = maxDailyRepeats,
                    quietHoursEnabled = quietEnabled,
                    quietHoursStart = quietStart,
                    quietHoursEnd = quietEnd
                )
            )
        }
    }
}
