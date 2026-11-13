package com.example.data

import android.content.Context
import android.content.Intent
import com.example.model.AppRule
import com.example.model.BlockerStyle
import org.json.JSONObject

object InstalledAppsHelper {
    private const val PREFS_NAME = "app_blocker_rules_prefs"
    private const val KEY_RULES_JSON = "saved_app_rules_json"

    fun loadAppRules(context: Context, fallback: List<AppRule>): List<AppRule> {
        val savedStyles = loadSavedStyles(context)
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        val queried = try {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        } catch (_: Exception) {
            emptyList()
        }

        val realApps = queried
            .asSequence()
            .mapNotNull { info ->
                val pkg = info.activityInfo?.packageName ?: return@mapNotNull null
                if (pkg == context.packageName) return@mapNotNull null
                val label = runCatching { info.loadLabel(pm).toString() }.getOrDefault(pkg).ifBlank { pkg }
                val style = savedStyles[pkg] ?: BlockerStyle.ALLOWED
                AppRule(
                    packageName = pkg,
                    appName = label,
                    blockerStyle = style,
                    isBlocked = style == BlockerStyle.STRICT
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
            .toList()

        if (realApps.isEmpty()) {
            return fallback.map { f ->
                val style = savedStyles[f.packageName] ?: f.blockerStyle
                f.copy(blockerStyle = style, isBlocked = style == BlockerStyle.STRICT)
            }
        }
        return realApps
    }

    fun saveRule(context: Context, packageName: String, style: BlockerStyle) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val obj = runCatching {
            JSONObject(prefs.getString(KEY_RULES_JSON, "{}") ?: "{}")
        }.getOrDefault(JSONObject())
        obj.put(packageName, style.name)
        prefs.edit().putString(KEY_RULES_JSON, obj.toString()).apply()
    }

    private fun loadSavedStyles(context: Context): Map<String, BlockerStyle> {
        val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_RULES_JSON, null) ?: return emptyMap()
        return try {
            val obj = JSONObject(raw)
            obj.keys().asSequence().associateWith { key ->
                runCatching { BlockerStyle.valueOf(obj.optString(key, "ALLOWED")) }.getOrDefault(BlockerStyle.ALLOWED)
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
