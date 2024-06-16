package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
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
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimeLoader
import org.bukkit.event.Event

@Description("Create a new default SlimePropertyMap")
@Examples("set {_slimePropertyMap} to default slime world property map")
@Since("1.0.0")
class ExprCreateSlimeProperties : SimpleExpression<SlimePropertyMap>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprCreateSlimeProperties::class.java,
				SlimePropertyMap::class.java,
				ExpressionType.SIMPLE,
				"[default] slime world property map"
			)
		}
	}

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime world property map"
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

	override fun getReturnType(): Class<SlimePropertyMap> {
		return SlimePropertyMap::class.java
	}

	@Suppress("unchecked_cast")
	override fun get(event: Event): Array<SlimePropertyMap> {
		val propertyMap = SlimePropertyMap()

		SlimePropertiesEnum.entries.forEach { propertyEnum ->
			when (propertyEnum.dataType) {
				"String" -> {
					val prop = propertyEnum.prop as SlimeProperty<String>
					propertyMap.setValue(prop, prop.defaultValue)
				}

				"Integer" -> {
					val prop = propertyEnum.prop as SlimeProperty<Int>
					propertyMap.setValue(prop, prop.defaultValue)
				}

				"Float" -> {
					val prop = propertyEnum.prop as SlimeProperty<Float>
					propertyMap.setValue(prop, prop.defaultValue)
				}

				"Boolean" -> {
					val prop = propertyEnum.prop as SlimeProperty<Boolean>
					propertyMap.setValue(prop, prop.defaultValue)
				}
			}
		}

		return arrayOf(propertyMap)
	}
}