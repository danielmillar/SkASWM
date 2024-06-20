package me.danielmillar.skaswm.util

import ch.njol.skript.Skript
import ch.njol.skript.command.EffectCommandEvent
import ch.njol.skript.lang.Expression
import com.infernalsuite.aswm.api.SlimePlugin
import com.infernalsuite.aswm.api.loaders.SlimeLoader
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimeLoader
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimePlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.Event

object Util {

	fun anyToInt(value: Any): Int? {
		return when (value) {
			is Int -> value

			is Long -> if (value in Int.MIN_VALUE..Int.MAX_VALUE) value.toInt() else null

			is String -> value.toIntOrNull()

			is Double -> if (value in Int.MIN_VALUE.toDouble()..Int.MAX_VALUE.toDouble()) value.toInt() else null
			is Float -> if (value in Int.MIN_VALUE.toFloat()..Int.MAX_VALUE.toFloat()) value.toInt() else null

			is Byte -> value.toInt()
			is Short -> value.toInt()

			else -> null
		}
	}

	fun anyToFloat(value: Any): Float? {
		return when (value) {
			is Float -> value

			is Int -> value.toFloat()
			is Long -> value.toFloat()

			is String -> value.toFloatOrNull()

			is Double -> value.toFloat()

			is Byte -> value.toFloat()
			is Short -> value.toFloat()

			else -> null
		}
	}

	fun anyToBoolean(value: Any): Boolean? {
		return when (value) {
			is Boolean -> value

			is Int -> value != 0
			is Long -> value != 0L
			is Float -> value != 0.0f
			is Double -> value != 0.0

			is String -> value.equals("true", ignoreCase = true) || value == "1"

			else -> null
		}
	}

	fun findValidDefaultSpawn(): Location {
		val defaultWorld = Bukkit.getWorlds()[0]
		val spawnLocation = defaultWorld.spawnLocation

		spawnLocation.y = 64.0
		while (spawnLocation.block.type != Material.AIR || spawnLocation.block.getRelative(BlockFace.UP).type != Material.AIR) {
			if (spawnLocation.y >= 320) {
				spawnLocation.add(0.0, 1.0, 0.0)
				break
			}

			spawnLocation.add(0.0, 1.0, 0.0)
		}
		return spawnLocation
	}

	fun setupEvent(event: Event): Pair<Player?, Pair<SlimePlugin, SlimeLoader>>? {
		var player: Player? = null
		if (event is EffectCommandEvent) player = event.sender as Player

		val slimePlugin = getSlimePlugin()
		val slimeLoader = getSlimeLoader()
		if (slimePlugin == null || slimeLoader == null) {
			player?.sendMessage("You must initialize Slime Plugin/Loader before using anything")
			Skript.error("You must initialize Slime Plugin/Loader before using anything")
			return null
		}

		return Pair(player, Pair(slimePlugin, slimeLoader))
	}

	fun checkWorldName(event: Event, worldName: Expression<String>, player: Player?): String? {
		val name = worldName.getSingle(event)
		if (name.isNullOrEmpty()) {
			player?.sendMessage("The world name cannot be null.")
			Skript.error("World name cannot be null.")
			return null
		}
		return name
	}
}