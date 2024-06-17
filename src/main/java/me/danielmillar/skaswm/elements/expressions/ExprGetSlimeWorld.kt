package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.command.EffectCommandEvent
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimeLoader
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimePlugin
import me.danielmillar.skaswm.util.ValidationUtil.validateSlime
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Event

class ExprGetSlimeWorld : SimpleExpression<World>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprGetSlimeWorld::class.java,
				World::class.java,
				ExpressionType.COMBINED,
				"[get] slime world with name %string%"
			)
		}
	}

	private lateinit var worldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime world with name ${worldName.getSingle(event)}"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		worldName = expressions[0] as Expression<String>
		return true
	}

	override fun isSingle(): Boolean {
		return true
	}

	override fun getReturnType(): Class<World> {
		return World::class.java
	}

	override fun get(event: Event): Array<World> {
		var player: Player? = null
		if(event is EffectCommandEvent) player = event.sender as Player

		val slimePlugin = getSlimePlugin()
		val slimeLoader = getSlimeLoader()
		if (!validateSlime(slimePlugin, slimeLoader)) {
			player?.sendMessage("You must initialize Slime Plugin/Loader before using anything")
			return emptyArray()
		}

		val worldName = worldName.getSingle(event)
		if (worldName.isNullOrEmpty()) {
			player?.sendMessage("The world name cannot be null.")
			Skript.error("World name cannot be null.")
			return emptyArray()
		}

		val bukkitWorld = Bukkit.getWorld(worldName)
		if(bukkitWorld == null){
			player?.sendMessage("World $worldName cannot be found, perhaps it doesn't exist or you didn't load it")
			Skript.error("World $worldName cannot be found, perhaps it doesn't exist or you didn't load it")
			return emptyArray()
		}

		return arrayOf(bukkitWorld)
	}
}