package me.danielmillar.skaswm.elements.conditions

import ch.njol.skript.Skript
import ch.njol.skript.lang.Condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.world.properties.SlimeProperty
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.elements.SlimePropertiesEnum
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.event.Event

class CondSlimeWorldProperty : Condition() {

	companion object {
		init {
			Skript.registerCondition(
				CondSlimeWorldProperty::class.java,
				"%slimeproperty% of slime world named %string% (1¦is|2¦is(n't| not)) %boolean/integer%"
			)
		}
	}

	private lateinit var slimePropertyType: Expression<SlimePropertiesEnum>
	private lateinit var worldName: Expression<String>
	private lateinit var value: Expression<*>

	override fun toString(event: Event?, debug: Boolean): String {
		return "${worldName.toString(event, debug)} property ${
			slimePropertyType.toString(
				event,
				debug
			)
		} ${if (isNegated) " is" else " isn't"} ${
			value.toString(
				event,
				debug
			)
		}"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		slimePropertyType = expressions[0] as Expression<SlimePropertiesEnum>
		worldName = expressions[1] as Expression<String>
		value = expressions[2]

		isNegated = parser.mark == 1
		return true
	}

	@Suppress("unchecked_cast")
	override fun check(event: Event): Boolean {
		val value = value.getSingle(event) ?: return false

		val setupResult = setupEvent(event) ?: return false
		val (player) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return false

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(worldName)
		if (worldData == null) {
			player?.sendMessage("World $worldName cannot be found in config")
			Skript.error("World $worldName cannot be found in config")
			return false
		}

		val property = slimePropertyType.getSingle(event)
		if (property == null) {
			Skript.error("Slime property is null")
			return false
		}

		return when (property.dataType) {
			"String" -> {
				val prop = property.prop as SlimeProperty<String>
				if (worldData.toPropertyMap().getValue(prop).equals(value)) isNegated else !isNegated
			}

			"Integer" -> {
				val prop = property.prop as SlimeProperty<Int>
				if (worldData.toPropertyMap().getValue(prop).equals(value)) isNegated else !isNegated
			}

			"Float" -> {
				val prop = property.prop as SlimeProperty<Float>
				if (worldData.toPropertyMap().getValue(prop).equals(value)) isNegated else !isNegated
			}

			"Boolean" -> {
				val prop = property.prop as SlimeProperty<Boolean>
				if (worldData.toPropertyMap().getValue(prop).equals(value)) isNegated else !isNegated
			}

			else -> {
				false
			}
		}
	}
}