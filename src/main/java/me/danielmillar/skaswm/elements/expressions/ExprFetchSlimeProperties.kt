package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.event.Event

class ExprFetchSlimeProperties : SimpleExpression<SlimePropertyMap>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprFetchSlimeProperties::class.java,
				SlimePropertyMap::class.java,
				ExpressionType.COMBINED,
				"fetch (properties|props) of (slimeworld|slime world) named %string%"
			)
		}
	}

	private lateinit var worldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Fetching properties of ${worldName.toString(event, debug)}"
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

	override fun getReturnType(): Class<SlimePropertyMap> {
		return SlimePropertyMap::class.java
	}

	override fun get(event: Event): Array<SlimePropertyMap> {
		val setupResult = setupEvent(event) ?: return emptyArray()

		val (player) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return emptyArray()

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(worldName)
		if (worldData == null) {
			player?.sendMessage("World $worldName cannot be found in config")
			Skript.error("World $worldName cannot be found in config")
			return emptyArray()
		}

		return arrayOf(worldData.toPropertyMap())
	}
}