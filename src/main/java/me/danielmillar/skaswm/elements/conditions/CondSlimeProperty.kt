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
import com.infernalsuite.aswm.api.world.properties.SlimeProperty
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.elements.SlimePropertiesEnum
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.event.Event

@Name("Check Slime Property")
@Description("Checks if the specified property in the property map is equal to the specified value.")
@Examples(
	value = [
		"if spawn x of slimePropertyMap is 5", "if allow monsters of slimePropertyMap isn't true"
	]
)
@Since("1.0.0")
class CondSlimeProperty : Condition() {

	companion object {
		init {
			Skript.registerCondition(
				CondSlimeProperty::class.java,
				"%slimeproperty% of %slimepropertymap% (1¦is|2¦is(n't| not)) %boolean/integer%"
			)
		}
	}

	private lateinit var slimeProperties: Expression<SlimePropertyMap>
	private lateinit var slimePropertyType: Expression<SlimePropertiesEnum>
	private lateinit var value: Expression<*>

	override fun toString(event: Event?, debug: Boolean): String {
		return "${slimeProperties.toString(event, debug)} ${if (isNegated) " is" else " isn't"} ${
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
		slimeProperties = expressions[1] as Expression<SlimePropertyMap>
		value = expressions[2]

		isNegated = parser.mark == 1
		return true
	}

	@Suppress("unchecked_cast")
	override fun check(event: Event): Boolean {
		val value = value.getSingle(event) ?: return false

		val setupResult = setupEvent(event) ?: return false
		val (player) = setupResult

		val properties = slimeProperties.getSingle(event)
		if (properties == null) {
			player?.sendMessage("Provided slime properties is null")
			Skript.error("Provided slime properties is null")
			return false
		}

		val property = slimePropertyType.getSingle(event)
		if (property == null) {
			player?.sendMessage("Slime property is null")
			Skript.error("Slime property is null")
			return false
		}

		return when (property.dataType) {
			"String" -> {
				val prop = property.prop as SlimeProperty<String>
				if (properties.getValue(prop).equals(value)) isNegated else !isNegated
			}

			"Integer" -> {
				val prop = property.prop as SlimeProperty<Int>
				if (properties.getValue(prop).equals(value)) isNegated else !isNegated
			}

			"Float" -> {
				val prop = property.prop as SlimeProperty<Float>
				if (properties.getValue(prop).equals(value)) isNegated else !isNegated
			}

			"Boolean" -> {
				val prop = property.prop as SlimeProperty<Boolean>
				if (properties.getValue(prop).equals(value)) isNegated else !isNegated
			}

			else -> {
				false
			}
		}
	}
}