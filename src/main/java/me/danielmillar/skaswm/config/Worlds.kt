package me.danielmillar.skaswm.config

import de.exlll.configlib.Configuration

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
}