package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.classes.Changer
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.world.properties.SlimeProperty
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.elements.SlimePropertiesEnum
import me.danielmillar.skaswm.util.CRUDUtil.handleBooleanProperty
import me.danielmillar.skaswm.util.CRUDUtil.handleFloatProperty
import me.danielmillar.skaswm.util.CRUDUtil.handleIntegerProperty
import me.danielmillar.skaswm.util.CRUDUtil.handleStringProperty
import me.danielmillar.skaswm.util.Util
import org.bukkit.event.Event

@Name("Change Slime Properties")
@Description("Modify a property value in a SlimePropertyMap.")
@Examples(
	value = [
		"set pvp of {_slimeProperty} to true",
		"set spawn x of {_slimeProperty} to 100"
	]
)
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
		return "${slimePropertyType.toString(event, debug)} of ${slimeProperties.toString(event, debug)}"
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

	override fun acceptChange(mode: Changer.ChangeMode): Array<Class<*>> {
		return when(mode){
			Changer.ChangeMode.SET, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE -> {
				arrayOf(Any::class.java)
			}
			else -> {
				Skript.error("Cannot $mode a property type")
				emptyArray()
			}
		}
	}

	@Suppress("unchecked_cast")
	override fun change(event: Event, delta: Array<Any?>, mode: Changer.ChangeMode) {
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
			"String" -> handleStringProperty(property, value.toString(), properties, mode)
			"Integer" -> handleIntegerProperty(property, value, properties, mode)
			"Float" -> handleFloatProperty(property, value, properties, mode)
			"Boolean" -> handleBooleanProperty(property, value, properties, mode)
			else -> Skript.error("Unknown property data type: ${property.dataType}")
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