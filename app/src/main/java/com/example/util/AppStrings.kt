package com.example.util

object AppStrings {
    var isArabic: Boolean = true

    fun get(ar: String, en: String): String = if (isArabic) ar else en

    // Navigation
    val quran: String get() = get("المصحف", "Quran")
    val adhkar: String get() = get("الأذكار", "Adhkar")
    val tasbeeh: String get() = get("السبحة", "Tasbeeh")
    val locker: String get() = get("القفل والورد", "Locker")
    val stats: String get() = get("الإحصائيات", "Stats")
    val settings: String get() = get("الإعدادات", "Settings")

    // General & Actions
    val appTitle: String get() = get("استقامة", "Istaqama")
    val searchSurah: String get() = get("ابحث عن سورة أو جزء...", "Search Surah or Juz...")
    val tafseer: String get() = get("التفسير الميسر", "Tafseer")
    val listen: String get() = get("استماع", "Listen")
    val play: String get() = get("تشغيل", "Play")
    val pause: String get() = get("إيقاف", "Pause")
    val save: String get() = get("حفظ", "Save")
    val cancel: String get() = get("إلغاء", "Cancel")
    val close: String get() = get("إغلاق", "Close")
    val complete: String get() = get("تم الإنجاز", "Completed")
    val emergencyBypass: String get() = get("تجاوز طوارئ", "Emergency Bypass")
    val confirmBypass: String get() = get("تأكيد التجاوز", "Confirm Bypass")
    val warningLockActive: String get() = get("الشاشة مقفلة لإتمام الورد اليومي", "Screen is locked to complete your daily ward")
    val emergencyDesc: String get() = get("هل أنت متأكد من تجاوز القفل؟ سيتم تسجيل التجاوز في إحصائياتك.", "Are you sure you want to bypass the lock? This will be recorded.")
    
    // Blocker styles
    val strictBlocker: String get() = get("قفل صارم", "Strict Lock")
    val overlayBlocker: String get() = get("شاشة حظر وتنبيه", "Overlay Banner")
    val gentleBlocker: String get() = get("تنبيه عائم لطيف", "Gentle Floating Reminder")
    val whitelistApps: String get() = get("التطبيقات المستثناة (الوايت ليست)", "Whitelisted Apps")
    val lockRules: String get() = get("مواعيد وقواعد القفل", "Lock Schedules")
    val newLockRule: String get() = get("إنشاء قفل جديد", "Create New Lock")
    val streakDays: String get() = get("أيام متتالية", "Days Streak")
    val khatmaProgress: String get() = get("تقدم الختمة", "Khatma Progress")
    val permissions: String get() = get("الصلاحيات المطلوبة", "Required Permissions")
    val calendarTitle: String get() = get("جدول القفل والورد", "Lock & Ward Calendar")
    val countdownRemaining: String get() = get("الوقت المتبقي للقفل", "Remaining until lock")
    val noUpcomingLock: String get() = get("لا يوجد قفل نشط قادم", "No upcoming lock active")
    val testLockNow: String get() = get("تجربة القفل الآن", "Test Lock Now")
    val scheduledForDay: String get() = get("الأقفال المجدولة", "Scheduled Locks")
    val noLocksForDay: String get() = get("لا توجد أقفال مجدولة لهذا اليوم", "No locks scheduled for this day")
    val dotQuran: String get() = get("ورد قرآن", "Quran Ward")
    val dotDhikr: String get() = get("أذكار", "Adhkar")
    val dotTasbeeh: String get() = get("تسبيح", "Tasbeeh")
    val recurrenceDaily: String get() = get("يومياً", "Daily")
    val strictKiosk: String get() = get("قفل كايوسيك مانع للخروج", "Kiosk Anti-Exit Lock")
    val batteryOptPermission: String get() = get("استثناء توفير البطارية للعمل بالخلفية", "Ignore Battery Optimizations")
}
