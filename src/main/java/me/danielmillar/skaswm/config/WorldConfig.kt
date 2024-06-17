package me.danielmillar.skaswm.config

import com.infernalsuite.aswm.api.world.properties.SlimeProperties.*
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import de.exlll.configlib.Configuration
import org.bukkit.Difficulty
import org.bukkit.World
import java.util.*

@Configuration
class WorldConfig {
	var spawnX: Int = 0
	var spawnY: Int = 64
	var spawnZ: Int = 0
	var spawnYaw: Float = 0F

	var difficulty: String = "peaceful"

	var allowMonsters: Boolean = true
	var allowAnimals: Boolean = true
	var dragonBattle: Boolean = false
	var pvp: Boolean = true

	var environment: String = "normal"
	var worldType: String = "default"
	var defaultBiome: String = "minecraft:plains"

	var readOnly: Boolean = false

	fun toPropertyMap(): SlimePropertyMap {
		try {
			enumValueOf<Difficulty>(difficulty.uppercase(Locale.getDefault()))
		} catch (ex: IllegalArgumentException) {
			throw IllegalArgumentException("unknown difficulty '" + this.difficulty + "'")
		}

		try {
			enumValueOf<World.Environment>(environment.uppercase(Locale.getDefault()))
		} catch (ex: IllegalArgumentException) {
			try {
				val envId = environment.toInt()

				if (envId < -1 || envId > 1) {
					throw NumberFormatException(environment)
				}

				environment = World.Environment.getEnvironment(envId)!!.name
			} catch (ex2: NumberFormatException) {
				throw IllegalArgumentException("unknown environment '" + this.environment + "'")
			}
		}

		val propertyMap = SlimePropertyMap()

		propertyMap.setValue(SPAWN_X, spawnX)
		propertyMap.setValue(SPAWN_Y, spawnY)
		propertyMap.setValue(SPAWN_Z, spawnZ)
		propertyMap.setValue(SPAWN_YAW, spawnYaw)

		propertyMap.setValue(DIFFICULTY, difficulty)
		propertyMap.setValue(ALLOW_MONSTERS, allowMonsters)
		propertyMap.setValue(ALLOW_ANIMALS, allowAnimals)
		propertyMap.setValue(DRAGON_BATTLE, dragonBattle)
		propertyMap.setValue(PVP, pvp)
		propertyMap.setValue(ENVIRONMENT, environment)
		propertyMap.setValue(WORLD_TYPE, worldType)
		propertyMap.setValue(DEFAULT_BIOME, defaultBiome)

		return propertyMap
	}

	companion object {
		fun fromPropertyMap(propertyMap: SlimePropertyMap, readOnly: Boolean): WorldConfig {
			val config = WorldConfig()

			config.spawnX = propertyMap.getValue(SPAWN_X) as Int
			config.spawnY = propertyMap.getValue(SPAWN_Y) as Int
			config.spawnZ = propertyMap.getValue(SPAWN_Z) as Int
			config.spawnYaw = propertyMap.getValue(SPAWN_YAW) as Float

			config.difficulty = propertyMap.getValue(DIFFICULTY) as String

			config.allowMonsters = propertyMap.getValue(ALLOW_MONSTERS) as Boolean
			config.allowAnimals = propertyMap.getValue(ALLOW_ANIMALS) as Boolean
			config.dragonBattle = propertyMap.getValue(DRAGON_BATTLE) as Boolean
			config.pvp = propertyMap.getValue(PVP) as Boolean
			config.environment = propertyMap.getValue(ENVIRONMENT) as String
			config.worldType = propertyMap.getValue(WORLD_TYPE) as String
			config.defaultBiome = propertyMap.getValue(DEFAULT_BIOME) as String

			config.readOnly = readOnly

			return config
		}
	}
}