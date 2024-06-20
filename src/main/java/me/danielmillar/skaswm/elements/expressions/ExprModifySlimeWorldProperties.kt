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
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.config.WorldConfig
import me.danielmillar.skaswm.config.Worlds
import me.danielmillar.skaswm.elements.SlimePropertiesEnum
import me.danielmillar.skaswm.util.CRUDUtil.handleBooleanProperty
import me.danielmillar.skaswm.util.CRUDUtil.handleFloatProperty
import me.danielmillar.skaswm.util.CRUDUtil.handleIntegerProperty
import me.danielmillar.skaswm.util.CRUDUtil.handleStringProperty
import me.danielmillar.skaswm.util.Util
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.event.Event

@Name("Change Slime Properties")
@Description("Modify a property value in a SlimePropertyMap from a world.", "Requires the world to be unloaded and loaded again to apply new properties!")
@Examples(
	value = [
		"set pvp of slime world named \"Test\" to true",
		"set spawn x slime world named \"Test\" to 100"
	]
)
@Since("1.0.0")
class ExprModifySlimeWorldProperties : SimpleExpression<Any>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprModifySlimeWorldProperties::class.java,
				Any::class.java,
				ExpressionType.SIMPLE,
				"%slimeproperty% of slime world named %string%"
			)
		}
	}

	private lateinit var worldName: Expression<String>
	private lateinit var slimePropertyType: Expression<SlimePropertiesEnum>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Update ${slimePropertyType.toString(event, debug)} of world named ${worldName.toString(event, debug)}"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		slimePropertyType = expressions[0] as Expression<SlimePropertiesEnum>
		worldName = expressions[1] as Expression<String>
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
		val setupResult = setupEvent(event) ?: return

		val (player) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(worldName)
		if (worldData == null) {
			player?.sendMessage("World $worldName cannot be found in config")
			Skript.error("World $worldName cannot be found in config")
			return
		}

		val properties = worldData.toPropertyMap()

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

		val newWorldData = WorldConfig.fromPropertyMap(properties, worldData.readOnly)
		SkASWM.getInstance().getConfigManager().getWorldConfig().setWorldConfig(worldName, newWorldData)
		SkASWM.getInstance().getConfigManager().saveWorldConfig()
	}

	override fun isSingle(): Boolean {
		return true
	}

	override fun getReturnType(): Class<Any> {
		return Any::class.java
	}

	@Suppress("unchecked_cast")
	override fun get(event: Event): Array<Any> {
		val setupResult = setupEvent(event) ?: return emptyArray()

		val (player) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return emptyArray()

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(worldName)
		if (worldData == null) {
			player?.sendMessage("World $worldName cannot be found in config")
			Skript.error("World $worldName cannot be found in config")
			return emptyArray()
		}

		val properties = worldData.toPropertyMap()

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