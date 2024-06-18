package me.danielmillar.skaswm.config

import de.exlll.configlib.Configuration
import org.bukkit.World

@Configuration
class Worlds {
	private var worlds: MutableMap<String, WorldConfig> = hashMapOf()

	fun getWorlds(): Map<String, WorldConfig> {
		return worlds.toMap()
	}

	fun setWorlds(worlds: Map<String, WorldConfig>) {
		this.worlds = worlds.toMutableMap()
	}

	fun getWorldConfig(name: String): WorldConfig? {
		return worlds[name]
	}

	fun setWorldConfig(name: String, config: WorldConfig) {
		worlds[name] = config
	}

	fun removeWorldConfig(name: String): WorldConfig? {
		return worlds.remove(name)
	}

	fun hasWorldConfig(name: String): Boolean {
		return worlds.containsKey(name)
	}

	fun updateWorldProperties(name: String, worldConfig: WorldConfig, world: World) {
		worldConfig.difficulty = world.difficulty.name.lowercase()
		worldConfig.allowMonsters = world.allowMonsters
		worldConfig.allowAnimals = world.allowAnimals
		worldConfig.pvp = world.pvp

		setWorldConfig(name, worldConfig)
	}
}