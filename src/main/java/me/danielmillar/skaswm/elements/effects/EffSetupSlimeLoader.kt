package me.danielmillar.skaswm.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.classes.EnumClassInfo
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.registrations.Classes
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.SlimePlugin
import com.infernalsuite.aswm.api.loaders.SlimeLoader
import me.danielmillar.skaswm.LoaderEnum
import org.bukkit.Bukkit
import org.bukkit.event.Event

class EffSetupSlimeLoader : Effect() {

	companion object {
		private lateinit var slimeLoader: SlimeLoader

		fun getSlimeLoader(): SlimeLoader? {
			if (!::slimeLoader.isInitialized) return null
			return slimeLoader
		}

		init {
			Classes.registerClass(
				EnumClassInfo(LoaderEnum::class.java, "slimeloader", "slime loaders")
					.user("loader(s)?")
					.name("Loader")
					.description("Represents a Slime loader.")
			)

			Skript.registerEffect(EffSetupSlimeLoader::class.java, "create slime loader with type %slimeloader%")
		}
	}

	private lateinit var loaderType: Expression<LoaderEnum>

	override fun toString(event: Event?, debug: Boolean): String {
		return "create slime loader with type ${loaderType.toString(event, debug)}"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		loaderType = expressions[0] as Expression<LoaderEnum>
		return true
	}

	override fun execute(event: Event) {
		val slimePlugin = EffSetupSlimePlugin.getSlimePlugin() ?: return
		slimeLoader = slimePlugin.getLoader(LoaderEnum.valueOf(loaderType.getSingle(event).toString()).toString())
	}
}