package me.danielmillar.skaswm.config

import ch.njol.skript.Skript
import de.exlll.configlib.Configuration
import me.danielmillar.skaswm.GameRuleEnum
import me.danielmillar.skaswm.util.Util
import org.bukkit.GameRule
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

	@Suppress("unchecked_cast")
	fun updateWorldProperties(name: String, worldConfig: WorldConfig, world: World) {
		worldConfig.difficulty = world.difficulty.name.lowercase()
		worldConfig.allowMonsters = world.allowMonsters
		worldConfig.allowAnimals = world.allowAnimals
		worldConfig.pvp = world.pvp

		val tempGameRules: MutableMap<String, String> = hashMapOf()
		GameRuleEnum.entries.forEach {
			when(it.dataType){
				"Boolean" -> {
					val prop = it.gameRule as GameRule<Boolean>
					val value = world.getGameRuleValue(it.gameRule)
					if(value != world.getGameRuleDefault(it.gameRule)){
						tempGameRules[it.name] = value.toString()
					}
				}

				"Integer" -> {
					val prop = it.gameRule as GameRule<Int>
					val value = world.getGameRuleValue(it.gameRule)
					if(value != world.getGameRuleDefault(it.gameRule)){
						tempGameRules[it.name] = value.toString()
					}
				}
			}
		}
		worldConfig.gamerules = tempGameRules

		setWorldConfig(name, worldConfig)
	}

	@Suppress("unchecked_cast")
	fun setWorldProperties(worldConfig: WorldConfig, world: World) {
		val gameRules = worldConfig.gamerules

		for (gameRule in gameRules) {
			val gameRuleEnum = GameRuleEnum.valueOf(gameRule.key)
			when(gameRuleEnum.dataType){
				"Boolean" -> {
					val prop = gameRuleEnum.gameRule as GameRule<Boolean>
					val convertedValue = gameRule.value.let { Util.anyToBoolean(it) } ?: run {
						Skript.error("Expected an Boolean value for property ${gameRuleEnum.name} but got null instead!")
						return
					}
					world.setGameRule(prop, convertedValue)
				}

				"Integer" -> {
					val prop = gameRuleEnum.gameRule as GameRule<Int>
					val convertedValue = gameRule.value.let { Util.anyToInt(it) } ?: run {
						Skript.error("Expected an Integer value for property ${gameRuleEnum.name} but got null instead!")
						return
					}
					world.setGameRule(prop, convertedValue)
				}
			}
		}
	}
}