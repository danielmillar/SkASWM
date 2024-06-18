package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.event.Event

@Name("Fetch Slime Worlds")
@Description("List all SlimeWorlds")
@Examples("set {slimeWorlds::*} to all slime worlds")
@Since("1.0.0")
class ExprFetchSlimeWorlds : SimpleExpression<String>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprFetchSlimeWorlds::class.java,
				String::class.java,
				ExpressionType.SIMPLE,
				"fetch all (slimeworlds|slime worlds)"
			)
		}
	}

	override fun toString(event: Event?, debug: Boolean): String {
		return "list of slimeworlds"
	}

	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		return true
	}

	override fun isSingle(): Boolean {
		return true
	}

	override fun getReturnType(): Class<String> {
		return String::class.java
	}

	override fun get(event: Event): Array<String> {
		val setupResult = setupEvent(event) ?: return emptyArray()

		val (_, slimeData) = setupResult
		val (_, slimeLoader) = slimeData

		return slimeLoader.listWorlds().toTypedArray()
	}
}