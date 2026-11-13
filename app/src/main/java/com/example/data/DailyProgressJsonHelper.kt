package com.example.data

import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

object DailyProgressJsonHelper {

    fun parseDayRecord(dateKey: String, obj: JSONObject): DailyProgressRecord {
        val prayersObj = obj.optJSONObject("prayers") ?: JSONObject()
        val prayers = mutableMapOf<String, PrayerDailyRecord>()
        prayersObj.keys().forEach { pid ->
            val pObj = prayersObj.getJSONObject(pid)
            prayers[pid] = PrayerDailyRecord(
                dateKey = dateKey,
                prayerId = pid,
                isPrayed = pObj.optBoolean("prayed", true),
                status = PrayerStatus.fromId(pObj.optString("status")),
                timestampMillis = pObj.optLong("time", System.currentTimeMillis()),
                notes = pObj.optString("notes", "")
            )
        }
        val actArr = obj.optJSONArray("activities") ?: JSONArray()
        val acts = mutableListOf<TinyActivityItem>()
        for (i in 0 until actArr.length()) {
            val a = actArr.getJSONObject(i)
            acts.add(TinyActivityItem(
                id = a.optString("id"),
                timestampMillis = a.optLong("time"),
                category = ActivityCategory.values().find { it.id == a.optString("cat") } ?: ActivityCategory.ALL,
                titleAr = a.optString("tAr"),
                titleEn = a.optString("tEn"),
                detailsAr = a.optString("dAr"),
                detailsEn = a.optString("dEn"),
                numericValue = a.optInt("num")
            ))
        }
        return DailyProgressRecord(
            dateKey = dateKey,
            prayersMarked = prayers,
            quranMinutes = obj.optInt("qMins"),
            ayahsRead = obj.optInt("ayahs"),
            tasbeehCount = obj.optInt("tasbeeh"),
            locksCompleted = obj.optInt("locks"),
            activities = acts
        )
    }

    fun serializeDayRecord(r: DailyProgressRecord): JSONObject {
        val obj = JSONObject()
        val pObj = JSONObject()
        r.prayersMarked.forEach { (pid, p) ->
            pObj.put(pid, JSONObject().apply {
                put("prayed", p.isPrayed); put("status", p.status.id)
                put("time", p.timestampMillis); put("notes", p.notes)
            })
        }
        val actArr = JSONArray()
        r.activities.take(30).forEach { a ->
            actArr.put(JSONObject().apply {
                put("id", a.id); put("time", a.timestampMillis); put("cat", a.category.id)
                put("tAr", a.titleAr); put("tEn", a.titleEn); put("dAr", a.detailsAr); put("dEn", a.detailsEn); put("num", a.numericValue)
            })
        }
        obj.put("prayers", pObj); obj.put("qMins", r.quranMinutes); obj.put("ayahs", r.ayahsRead)
        obj.put("tasbeeh", r.tasbeehCount); obj.put("locks", r.locksCompleted); obj.put("activities", actArr)
        return obj
    }
}
