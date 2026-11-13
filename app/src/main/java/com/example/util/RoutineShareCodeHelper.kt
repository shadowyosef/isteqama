package com.example.util

import android.content.Context
import android.content.Intent
import android.util.Base64
import com.example.data.ScreenLockRepository
import com.example.data.TasbeehScheduleRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.TasbeehScheduleModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object RoutineShareCodeHelper {
    private const val PREFIX = "ISTAQAMA1:"

    fun exportPackageCode(): String {
        val root = JSONObject()
        val locksArr = JSONArray()
        ScreenLockRepository.locks.value.take(12).forEach { lock ->
            locksArr.put(JSONObject().apply {
                put("t", lock.title)
                put("k", lock.lockType)
                put("n", lock.targetAmount)
                put("h", lock.triggerHour)
                put("m", lock.triggerMinute)
                put("c", lock.contentConfigJson)
                put("s", lock.schedulesJson)
            })
        }
        val schedsArr = JSONArray()
        TasbeehScheduleRepository.schedules.value.take(10).forEach { s ->
            schedsArr.put(JSONObject().apply {
                put("ar", s.phraseAr)
                put("en", s.phraseEn)
                put("c", s.targetCount)
                put("sec", s.repeatIntervalSeconds)
            })
        }
        root.put("locks", locksArr)
        root.put("tasbeeh", schedsArr)
        val encoded = Base64.encodeToString(root.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return "$PREFIX$encoded"
    }

    fun importPackageCode(raw: String): Result<Int> = runCatching {
        val payload = raw.trim().removePrefix(PREFIX)
        val jsonStr = String(Base64.decode(payload, Base64.DEFAULT), Charsets.UTF_8)
        val root = JSONObject(jsonStr)
        var imported = 0
        val locksArr = root.optJSONArray("locks") ?: JSONArray()
        for (i in 0 until locksArr.length()) {
            val obj = locksArr.getJSONObject(i)
            ScreenLockRepository.addLock(
                ScreenLockEntity(
                    id = "imp_${UUID.randomUUID().toString().take(8)}",
                    title = obj.optString("t", "ورد مستورد"),
                    lockType = obj.optString("k", "DHIKR"),
                    targetAmount = obj.optInt("n", 7),
                    triggerHour = obj.optInt("h", 6),
                    triggerMinute = obj.optInt("m", 0),
                    contentConfigJson = obj.optString("c", ""),
                    schedulesJson = obj.optString("s", ""),
                    isEnabled = true
                )
            )
            imported++
        }
        val schedArr = root.optJSONArray("tasbeeh") ?: JSONArray()
        for (i in 0 until schedArr.length()) {
            val obj = schedArr.getJSONObject(i)
            TasbeehScheduleRepository.addSchedule(
                TasbeehScheduleModel(
                    phraseAr = obj.optString("ar", "سُبْحَانَ اللَّهِ"),
                    phraseEn = obj.optString("en", "Subhan Allah"),
                    targetCount = obj.optInt("c", 33),
                    repeatIntervalSeconds = obj.optInt("sec", 900)
                )
            )
            imported++
        }
        imported
    }

    fun shareCode(context: Context, code: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "كود باقة أذكار وختمة الاستقامة:\n$code")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة كود الروتين").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun buildQrMatrix(data: String, size: Int = 21): Array<BooleanArray> {
        val m = Array(size) { BooleanArray(size) }
        fun markFinder(r0: Int, c0: Int) {
            for (r in 0..6) for (c in 0..6) {
                m[r0 + r][c0 + c] = (r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4))
            }
        }
        markFinder(0, 0); markFinder(0, size - 7); markFinder(size - 7, 0)
        val bytes = data.toByteArray()
        var idx = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= size - 8
                val inBotLeft = r >= size - 8 && c < 8
                if (!inTopLeft && !inTopRight && !inBotLeft) {
                    val b = bytes[idx % bytes.size.coerceAtLeast(1)].toInt()
                    m[r][c] = ((b ushr (idx % 7)) xor (r * 3 + c * 5)) and 1 == 1
                    idx++
                }
            }
        }
        return m
    }
}
