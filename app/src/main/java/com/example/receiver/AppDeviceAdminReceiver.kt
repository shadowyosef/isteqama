package com.example.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.SecurityRepository
import com.example.util.AppStrings

class AppDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(
            context,
            AppStrings.get("تم تفعيل صلاحية إدارة الجهاز لحماية القفل الإيماني", "Device Admin enabled for spiritual lock protection"),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return if (SecurityRepository.adminLockEnforced.value) {
            AppStrings.get(
                "تحذير استقامة: إلغاء صلاحية مسؤول الجهاز يتطلب التحقق من البصمة أو رمز PIN لمنع إيقاف التطبيق أو حذفه أثناء القفل النشط.",
                "Istaqama Warning: Disabling Device Admin requires Biometric or PIN authentication to prevent stopping or uninstalling active locks."
            )
        } else {
            AppStrings.get("إلغاء صلاحية إدارة الجهاز سيعطل حماية القفل الإجباري.", "Disabling Device Admin disables strict lock protection.")
        }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(
            context,
            AppStrings.get("تم إلغاء تفعيل صلاحية إدارة الجهاز", "Device Admin disabled"),
            Toast.LENGTH_SHORT
        ).show()
    }
}
