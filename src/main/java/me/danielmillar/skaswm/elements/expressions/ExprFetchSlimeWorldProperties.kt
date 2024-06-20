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
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.World
import org.bukkit.event.Event

@Name("Fetch slime world properties")
@Description("Fetch slime properties using a slime world.")
@Examples(
	value = [
		"set {_slimeProps} to fetch props of {_world}"
	]
)
@Since("1.0.0")
class ExprFetchSlimeWorldProperties : SimpleExpression<SlimePropertyMap>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprFetchSlimeWorldProperties::class.java,
				SlimePropertyMap::class.java,
				ExpressionType.COMBINED,
				"fetch (properties|props) of %world%"
			)
		}
	}

	private lateinit var worldExpr: Expression<World>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Fetching properties of ${worldExpr.toString(event, debug)}"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		worldExpr = expressions[0] as Expression<World>
		return true
	}

	override fun isSingle(): Boolean {
		return true
	}

	override fun getReturnType(): Class<SlimePropertyMap> {
		return SlimePropertyMap::class.java
	}

	override fun get(event: Event): Array<SlimePropertyMap> {
		val setupResult = setupEvent(event) ?: return emptyArray()

		val (player) = setupResult

		val world = worldExpr.getSingle(event)
		if (world == null) {
			player?.sendMessage("The world cannot be null.")
			Skript.error("World cannot be null.")
			return emptyArray()
		}

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(world.name)
		if (worldData == null) {
			player?.sendMessage("World ${world.name} cannot be found in config")
			Skript.error("World ${world.name} cannot be found in config")
			return emptyArray()
		}

		return arrayOf(worldData.toPropertyMap())
	}
}