package me.danielmillar.skaswm.elements

import ch.njol.skript.classes.ClassInfo
import ch.njol.skript.classes.EnumClassInfo
import ch.njol.skript.registrations.Classes
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
			)
		}
	}
}