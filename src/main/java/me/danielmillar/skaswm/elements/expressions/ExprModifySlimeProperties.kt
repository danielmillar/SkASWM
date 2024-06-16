package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.classes.Changer
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.world.properties.SlimeProperty
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.elements.SlimePropertiesEnum
import me.danielmillar.skaswm.util.Util
import org.bukkit.event.Event

@Description("Modify a property value of SlimePropertyMap")
@Examples("set pvp of {_slimePropertyMap} to true")
@Since("1.0.0")
class ExprModifySlimeProperties : SimpleExpression<Any>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprModifySlimeProperties::class.java,
				Any::class.java,
				ExpressionType.SIMPLE,
				"%slimeproperty% of %slimepropertymap%"
			)
		}
	}

	private lateinit var slimeProperties: Expression<SlimePropertyMap>
	private lateinit var slimePropertyType: Expression<SlimePropertiesEnum>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime world property map value"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		slimePropertyType = expressions[0] as Expression<SlimePropertiesEnum>
		slimeProperties = expressions[1] as Expression<SlimePropertyMap>
		return true
	}

	override fun acceptChange(mode: Changer.ChangeMode?): Array<Class<*>> {
		if (mode != Changer.ChangeMode.SET) {
			Skript.error("Cannot ${mode.toString()} a property type")
			return emptyArray()
		}
		return arrayOf(Any::class.java)
	}

	@Suppress("unchecked_cast")
	override fun change(event: Event, delta: Array<Any?>, mode: Changer.ChangeMode) {
		if (mode != Changer.ChangeMode.SET) return

		val properties = slimeProperties.getSingle(event)
		if (properties == null) {
			Skript.error("Provided slime properties is null")
			return
		}

		val property = slimePropertyType.getSingle(event)
		if (property == null) {
			Skript.error("Slime property is null")
			return
		}

		val value = delta[0]
		if (value == null) {
			Skript.error("Provided value is null")
			return
		}

		when (property.dataType) {
			"String" -> {
				val prop = property.prop as SlimeProperty<String>
				properties.setValue(prop, value.toString())
			}

			"Integer" -> {
				try {
					val prop = property.prop as SlimeProperty<Int>
					val convertedValue = Util.anyToInt(value) ?: run {
						Skript.error("Expected an Int value for property ${property.name} but got null instead!")
						return
					}
					properties.setValue(prop, convertedValue)
				} catch (e: NumberFormatException) {
					Skript.error("Expected an Integer value for property ${property.name}")
				}
			}

			"Float" -> {
				try {
					val prop = property.prop as SlimeProperty<Float>
					val convertedValue = Util.anyToFloat(value) ?: run {
						Skript.error("Expected an Float value for property ${property.name} but got null instead!")
						return
					}
					properties.setValue(prop, convertedValue)
				} catch (e: NumberFormatException) {
					Skript.error("Expected an Integer value for property ${property.name}")
				}
			}

			"Boolean" -> {
				try {
					val prop = property.prop as SlimeProperty<Boolean>
					val convertedValue = Util.anyToBoolean(value) ?: run {
						Skript.error("Expected an Boolean value for property ${property.name} but got null instead!")
						return
					}
					properties.setValue(prop, convertedValue)
				} catch (e: NumberFormatException) {
					Skript.error("Expected an Integer value for property ${property.name}")
				}
			}

			else -> {
				Skript.error("Unknown property data type: ${property.dataType}")
			}
		}
	}

	override fun isSingle(): Boolean {
		return true
	}

	override fun getReturnType(): Class<Any> {
		return Any::class.java
	}

	@Suppress("unchecked_cast")
	override fun get(event: Event): Array<Any> {
		val properties = slimeProperties.getSingle(event) ?: return emptyArray()
		val property = slimePropertyType.getSingle(event) ?: return emptyArray()

		return when (property.dataType) {
			"String" -> {
				val prop = property.prop as SlimeProperty<String>
				arrayOf(properties.getValue(prop))
			}

			"Integer" -> {
				val prop = property.prop as SlimeProperty<Int>
				arrayOf(properties.getValue(prop))
			}

			"Float" -> {
				val prop = property.prop as SlimeProperty<Float>
				arrayOf(properties.getValue(prop))
			}

			"Boolean" -> {
				val prop = property.prop as SlimeProperty<Boolean>
				arrayOf(properties.getValue(prop))
			}

			else -> {
				Skript.error("Unknown property data type: ${property.dataType}")
				emptyArray()
			}
		}
	}
}