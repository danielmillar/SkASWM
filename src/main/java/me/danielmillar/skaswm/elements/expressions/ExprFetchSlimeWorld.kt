package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Event

class ExprFetchSlimeWorld : SimpleExpression<World>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprFetchSlimeWorld::class.java,
				World::class.java,
				ExpressionType.COMBINED,
				"fetch (slimeworld|slime world) named %string%"
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
		val setupResult = setupEvent(event) ?: return emptyArray()

		val (player) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return emptyArray()

		val bukkitWorld = Bukkit.getWorld(worldName)
		if (bukkitWorld == null) {
			player?.sendMessage("World $worldName cannot be found, perhaps it doesn't exist or you didn't load it")
			Skript.error("World $worldName cannot be found, perhaps it doesn't exist or you didn't load it")
			return emptyArray()
		}

		return arrayOf(bukkitWorld)
	}
}