package com.example.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.data.DailyProgressRepository
import com.example.data.TasbeehRepository

@RequiresApi(Build.VERSION_CODES.N)
class QuickTasbeehTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        DailyProgressRepository.init(this)
        TasbeehRepository.increment()
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val count = TasbeehRepository.totalLifetimeCount.value
        tile.label = "تسبيح ($count)"
        tile.state = Tile.STATE_ACTIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = TasbeehRepository.getCurrentItem().phraseAr.take(18)
        }
        tile.updateTile()
    }
}
