package me.danielmillar.skaswm.elements

import ch.njol.skript.classes.EnumClassInfo
import ch.njol.skript.registrations.Classes
import org.bukkit.block.Biome

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
		}
	}
}