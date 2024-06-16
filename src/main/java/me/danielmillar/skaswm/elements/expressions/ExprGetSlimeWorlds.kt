package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimeLoader
import org.bukkit.event.Event

class ExprGetSlimeWorlds : SimpleExpression<Int>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprGetSlimeWorlds::class.java,
				Int::class.javaObjectType,
				ExpressionType.SIMPLE,
				"[get] number of slime worlds"
			)
		}
	}

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime loader: ${getSlimeLoader()}, number of worlds"
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

	override fun getReturnType(): Class<Int> {
		return Int::class.javaObjectType
	}

	override fun get(event: Event): Array<Int> {
		val slimeLoader = getSlimeLoader() ?: return emptyArray()
		return arrayOf(slimeLoader.listWorlds().size)
	}
}