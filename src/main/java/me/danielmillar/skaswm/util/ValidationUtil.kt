package me.danielmillar.skaswm.util

import ch.njol.skript.Skript
import com.infernalsuite.aswm.api.SlimePlugin
import com.infernalsuite.aswm.api.loaders.SlimeLoader
import org.bukkit.entity.Player

object ValidationUtil {

	fun validateSlime(slimePlugin: SlimePlugin?, slimeLoader: SlimeLoader?): Boolean {
		if (slimePlugin == null || slimeLoader == null) {
			Skript.error("You must initialize Slime Plugin/Loader before using anything")
			return false
		}
		return true
	}

	fun validateInputs(slimePlugin: Any?, slimeLoader: Any?, worldName: String?, player: Player?): Boolean {

		if (worldName.isNullOrEmpty()) {
			player?.sendMessage("The world name cannot be null.")
			Skript.error("World name cannot be null.")
			return false
		}

		return true
	}

}