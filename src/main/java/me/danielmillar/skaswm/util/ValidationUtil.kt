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
}