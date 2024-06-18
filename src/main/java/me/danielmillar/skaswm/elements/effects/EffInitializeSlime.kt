package me.danielmillar.skaswm.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.SlimePlugin
import com.infernalsuite.aswm.api.loaders.SlimeLoader
import me.danielmillar.skaswm.elements.SlimeLoaderTypeEnum
import org.bukkit.Bukkit
import org.bukkit.event.Event

@Name("Initialize Slime")
@Description("Initializes the SlimePlugin API and sets up a SlimeLoader based on the type")
@Examples(
	"initialize slime plugin with loader file", "initialize slime plugin with loader type file"
)
@Since("1.0.0")
class EffInitializeSlime : Effect() {

	companion object {
		private lateinit var slimePlugin: SlimePlugin
		private lateinit var slimeLoader: SlimeLoader

		fun getSlimeLoader(): SlimeLoader? {
			if (::slimeLoader.isInitialized) return slimeLoader
			return null
		}

		fun getSlimePlugin(): SlimePlugin? {
			if (::slimePlugin.isInitialized) return slimePlugin
			return null
		}

		init {
			Skript.registerEffect(
				EffInitializeSlime::class.java,
				"initialize slime plugin with loader [type] %slimeloader%"
			)
		}
	}

	private lateinit var loaderType: Expression<SlimeLoaderTypeEnum>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Initialize slime plugin with loader type ${loaderType.toString(event, debug)}"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		loaderType = expressions[0] as Expression<SlimeLoaderTypeEnum>
		return true
	}

	override fun execute(event: Event) {
		val bukkitPlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager") ?: return
		slimePlugin = bukkitPlugin as SlimePlugin
		slimeLoader = slimePlugin.getLoader(
			SlimeLoaderTypeEnum.valueOf(loaderType.getSingle(event).toString()).toString().lowercase()
		)
	}
}