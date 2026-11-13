package com.example.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.CompletedLockRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

object ScreenLockRepository {
    private const val PREFS = "screen_lock_repo_prefs"
    private const val KEY_SEEDED = "default_locks_seeded_v2"
    private val scope = CoroutineScope(Dispatchers.IO)
    private var database: AppDatabase? = null
    var appContext: Context? = null

    private val _locks = MutableStateFlow<List<ScreenLockEntity>>(emptyList())
    val locks: StateFlow<List<ScreenLockEntity>> = _locks.asStateFlow()

    private val _activeLocksQueue = MutableStateFlow<List<ScreenLockEntity>>(emptyList())
    val activeLocksQueue: StateFlow<List<ScreenLockEntity>> = _activeLocksQueue.asStateFlow()
    val currentActiveLock = MutableStateFlow<ScreenLockEntity?>(null)

    private var lastRecordedDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    private val _completedLocksToday = MutableStateFlow<List<CompletedLockRecord>>(emptyList())
    val completedLocksToday: StateFlow<List<CompletedLockRecord>> = _completedLocksToday.asStateFlow()

    val COLOR_QURAN = Color(0xFF2E7D32)
    val COLOR_DHIKR = Color(0xFFFFA000)
    val COLOR_TASBEEH = Color(0xFF1976D2)

    fun init(context: Context) {
        val ctx = context.applicationContext
        appContext = ctx
        if (com.example.util.LockPersistenceManager.isLockActive(ctx)) {
            val restored = com.example.util.LockPersistenceManager.restoreActiveLock(ctx)
            if (restored != null && currentActiveLock.value == null) {
                _activeLocksQueue.value = listOf(restored)
                currentActiveLock.value = restored
            }
        }
        if (database != null) return
        val db = AppDatabase.getDatabase(ctx)
        database = db
        scope.launch {
            val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            db.screenLockDao().getAllLocksFlow().collect { list ->
                if (!prefs.getBoolean(KEY_SEEDED, false)) {
                    prefs.edit().putBoolean(KEY_SEEDED, true).apply()
                    ScreenLockDefaultsHelper.seedDefaultsIfMissing(db, list)
                } else {
                    _locks.value = list
                }
            }
        }
    }

    fun addLock(lock: ScreenLockEntity) {
        appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()?.putBoolean(KEY_SEEDED, true)?.apply()
        scope.launch { database?.screenLockDao()?.insertLock(lock) }
    }

    fun updateLock(lock: ScreenLockEntity) { scope.launch { database?.screenLockDao()?.updateLock(lock) } }

    fun toggleLock(id: String) {
        val item = _locks.value.find { it.id == id } ?: return
        setLockEnabled(id, !item.isEnabled)
    }

    fun setLockEnabled(id: String, enabled: Boolean) {
        val item = _locks.value.find { it.id == id } ?: return
        val updated = item.copy(isEnabled = enabled)
        _locks.value = _locks.value.map { if (it.id == id) updated else it }
        updateLock(updated)
        if (!updated.isEnabled && currentActiveLock.value?.id == id) dismissLock()
    }

    fun deleteLock(id: String) {
        appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()?.putBoolean(KEY_SEEDED, true)?.apply()
        _locks.value = _locks.value.filterNot { it.id == id }
        if (currentActiveLock.value?.id == id) dismissLock()
        scope.launch { database?.screenLockDao()?.deleteLockById(id) }
    }

    fun triggerLock(lock: ScreenLockEntity, context: Context? = null) {
        val targetCtx = context?.applicationContext ?: appContext
        if (_activeLocksQueue.value.none { it.id == lock.id }) _activeLocksQueue.value += lock
        val act = _activeLocksQueue.value.firstOrNull()
        currentActiveLock.value = act
        act?.let { targetCtx?.let { ctx -> com.example.util.LockPersistenceManager.saveActiveLock(ctx, it) } }
    }

    fun triggerLocks(locks: List<ScreenLockEntity>, context: Context? = null) {
        val targetCtx = context?.applicationContext ?: appContext
        val existingIds = _activeLocksQueue.value.map { it.id }.toSet()
        _activeLocksQueue.value += locks.filter { it.id !in existingIds }
        val act = _activeLocksQueue.value.firstOrNull()
        currentActiveLock.value = act
        act?.let { targetCtx?.let { ctx -> com.example.util.LockPersistenceManager.saveActiveLock(ctx, it) } }
    }

    fun dismissLock(context: Context? = null) {
        val targetCtx = context?.applicationContext ?: appContext
        val cur = currentActiveLock.value
        if (cur != null) _activeLocksQueue.value = _activeLocksQueue.value.filter { it.id != cur.id }
        val next = _activeLocksQueue.value.firstOrNull()
        currentActiveLock.value = next
        targetCtx?.let { ctx ->
            if (next != null) com.example.util.LockPersistenceManager.saveActiveLock(ctx, next)
            else com.example.util.LockPersistenceManager.clearActiveLock(ctx)
        }
    }

    fun resetProgressToStart(lockId: String) {
        val lock = _locks.value.find { it.id == lockId } ?: currentActiveLock.value?.takeIf { it.id == lockId } ?: return
        val updatedLock = com.example.util.LockProgressHelper.resetToStart(lock)
        updateLock(updatedLock)
        if (currentActiveLock.value?.id == lockId) {
            currentActiveLock.value = updatedLock
            appContext?.let { ctx -> com.example.util.LockPersistenceManager.saveActiveLock(ctx, updatedLock) }
        }
    }

    fun recordLockCompleted(lock: ScreenLockEntity) {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (currentDay != lastRecordedDay) { lastRecordedDay = currentDay; _completedLocksToday.value = emptyList() }
        _completedLocksToday.value = listOf(CompletedLockRecord("c_${System.currentTimeMillis()}", lock.title, lock.lockType, lock.targetAmount)) + _completedLocksToday.value
    }

    fun resetCompletedLocksToday() { _completedLocksToday.value = emptyList() }
    fun advanceProgress(lockId: String) { _locks.value.find { it.id == lockId }?.let { updateLock(com.example.util.LockProgressHelper.advanceProgress(it)) } }
    fun getDotsForDay(dayOfWeek: Int, dateStr: String): List<Color> = com.example.util.ScreenLockScheduleHelper.getDotsForDay(_locks.value, dayOfWeek, dateStr)
    fun getRemainingMillisToNextLock(context: Context? = null): Pair<ScreenLockEntity?, Long> = com.example.util.ScreenLockScheduleHelper.getRemainingMillisToNextLock(_locks.value, context ?: appContext)
}
