package me.danielmillar.skaswm.elements.conditions

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.event.Event

@Name("Check Slime World Loaded")
@Description("Checks if the specified world is loaded or not.")
@Examples(
	value = [
		"if slime world named \"exampleWorld\" is loaded",
		"if slime world named \"exampleWorld\" isn't loaded"
	]
)
@Since("1.0.0")
class CondSlimeWorldLoaded : Condition() {

	companion object {
		init {
			Skript.registerCondition(
				CondSlimeWorldLoaded::class.java,
				"slime world named %string% (1¦is|2¦is(n't| not)) loaded"
			)
		}
	}

	private lateinit var worldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "${worldName.toString(event, debug)} ${if (isNegated) " is" else " isn't"} loaded"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		worldName = expressions[0] as Expression<String>
		isNegated = parser.mark == 1
		return true
	}

	override fun check(event: Event): Boolean {
		val setupResult = setupEvent(event) ?: return false
		val (player) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return false
		val bukkitWorld = Bukkit.getWorld(worldName)

		return if (bukkitWorld != null) isNegated else !isNegated
	}
}