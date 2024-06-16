package me.danielmillar.skaswm.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.SlimePlugin
import org.bukkit.Bukkit
import org.bukkit.event.Event

class EffSetupSlimePlugin : Effect() {

	companion object {
		private lateinit var slimePlugin: SlimePlugin

		fun getSlimePlugin(): SlimePlugin? {
			if (!::slimePlugin.isInitialized) return null
			return slimePlugin
		}

		init {
			Skript.registerEffect(EffSetupSlimePlugin::class.java, "create instance of slime plugin")
		}
	}

	override fun toString(event: Event?, debug: Boolean): String {
		return "create instance of slime plugin: $slimePlugin"
	}

	override fun init(expressions: Array<Expression<*>>, matchedPattern: Int, isDelayed: Kleenean, parser: SkriptParser.ParseResult): Boolean {
		return true
	}

	override fun execute(event: Event) {
		val bukkitPlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager") ?: return
		slimePlugin = bukkitPlugin as SlimePlugin
	}
}