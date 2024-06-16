package me.danielmillar.skaswm.elements

import ch.njol.skript.classes.ClassInfo
import ch.njol.skript.classes.EnumClassInfo
import ch.njol.skript.classes.Parser
import ch.njol.skript.lang.ParseContext
import ch.njol.skript.registrations.Classes
import com.infernalsuite.aswm.api.world.properties.SlimeProperties
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap

class Types {
	companion object {
		init {
			Classes.registerClass(
				EnumClassInfo(SlimeLoaderTypeEnum::class.java, "slimeloader", "slime loaders")
					.user("loaders")
					.name("Loader")
					.description("Represents a Slime loader.")
					.since("1.0.0")
			)

			Classes.registerClass(
				EnumClassInfo(SlimePropertiesEnum::class.java, "slimeproperty", "slime properties")
					.user("properties")
					.since("1.0.0")
			)

			Classes.registerClass(
				ClassInfo(SlimePropertyMap::class.java, "slimepropertymap")
					.since("1.0.0")
					.parser(object : Parser<SlimePropertyMap>() {
						override fun canParse(context: ParseContext?): Boolean {
							return false
						}

						override fun toString(slimePropertyMap: SlimePropertyMap?, flags: Int): String {
							return toVariableNameString(slimePropertyMap)
						}

						override fun toVariableNameString(slimePropertyMap: SlimePropertyMap?): String {
							return slimePropertyMap?.let {
								"SlimePropertyMap{" +
										"spawnX=${it.getValue(SlimeProperties.SPAWN_X)}," +
										"spawnY=${it.getValue(SlimeProperties.SPAWN_Y)}," +
										"spawnZ=${it.getValue(SlimeProperties.SPAWN_Z)}," +
										"spawnYaw=${it.getValue(SlimeProperties.SPAWN_YAW)}," +
										"difficulty=${it.getValue(SlimeProperties.DIFFICULTY)}," +
										"allowMonsters=${it.getValue(SlimeProperties.ALLOW_MONSTERS)}," +
										"allowAnimals=${it.getValue(SlimeProperties.ALLOW_ANIMALS)}," +
										"dragonBattle=${it.getValue(SlimeProperties.DRAGON_BATTLE)}," +
										"pvp=${it.getValue(SlimeProperties.PVP)}," +
										"environment=${it.getValue(SlimeProperties.ENVIRONMENT)}," +
										"worldType=${it.getValue(SlimeProperties.WORLD_TYPE)}," +
										"defaultBiome=${it.getValue(SlimeProperties.DEFAULT_BIOME)}" +
										"}"
							} ?: "SlimePropertyMap is null"
						}
					})
			)
		}
	}
}