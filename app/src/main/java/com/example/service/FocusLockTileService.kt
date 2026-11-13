package com.example.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.data.FocusProfileMode
import com.example.data.FocusProfilesRepository

@RequiresApi(Build.VERSION_CODES.N)
class FocusLockTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        FocusProfilesRepository.init(this)
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        FocusProfilesRepository.init(this)
        val current = FocusProfilesRepository.activeProfile.value
        val next = if (current == FocusProfileMode.NONE) FocusProfileMode.STUDY_WORK else FocusProfileMode.NONE
        FocusProfilesRepository.setProfile(this, next)
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val active = FocusProfilesRepository.effectiveProfile()
        val enabled = active != FocusProfileMode.NONE
        tile.state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = if (enabled) active.labelAr else "تركيز إسلامي"
        tile.updateTile()
    }
}
