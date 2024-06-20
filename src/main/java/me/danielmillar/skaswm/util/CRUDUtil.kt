package me.danielmillar.skaswm.util

import ch.njol.skript.Skript
import ch.njol.skript.classes.Changer
import com.infernalsuite.aswm.api.world.properties.SlimeProperty
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.elements.SlimePropertiesEnum

object CRUDUtil {

	@Suppress("unchecked_cast")
	fun handleStringProperty(property: SlimePropertiesEnum, value: String, properties: SlimePropertyMap, mode: Changer.ChangeMode) {
		if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) return
		val prop = property.prop as SlimeProperty<String>
		properties.setValue(prop, value)
	}

	@Suppress("unchecked_cast")
	fun handleIntegerProperty(property: SlimePropertiesEnum, value: Any, properties: SlimePropertyMap, mode: Changer.ChangeMode) {
		try {
			val prop = property.prop as SlimeProperty<Int>
			val convertedValue = Util.anyToInt(value) ?: run {
				Skript.error("Expected an Int value for property ${property.name} but got null instead!")
				return
			}

			when (mode) {
				Changer.ChangeMode.SET -> {
					properties.setValue(prop, convertedValue)
				}
				Changer.ChangeMode.ADD -> {
					val currentValue = properties.getValue(prop)
					properties.setValue(prop, currentValue + convertedValue)
				}
				Changer.ChangeMode.REMOVE -> {
					val currentValue = properties.getValue(prop)
					properties.setValue(prop, currentValue - convertedValue)
				}
				else -> TODO()
			}
		} catch (e: NumberFormatException) {
			Skript.error("Expected an Integer value for property ${property.name}")
		}
	}

	@Suppress("unchecked_cast")
	fun handleFloatProperty(property: SlimePropertiesEnum, value: Any, properties: SlimePropertyMap, mode: Changer.ChangeMode) {
		try {
			val prop = property.prop as SlimeProperty<Float>
			val convertedValue = Util.anyToFloat(value) ?: run {
				Skript.error("Expected a Float value for property ${property.name} but got null instead!")
				return
			}

			when (mode) {
				Changer.ChangeMode.SET -> {
					properties.setValue(prop, convertedValue)
				}
				Changer.ChangeMode.ADD -> {
					val currentValue = properties.getValue(prop)
					properties.setValue(prop, currentValue + convertedValue)
				}
				Changer.ChangeMode.REMOVE -> {
					val currentValue = properties.getValue(prop)
					properties.setValue(prop, currentValue - convertedValue)
				}
				else -> TODO()
			}
		} catch (e: NumberFormatException) {
			Skript.error("Expected a Float value for property ${property.name}")
		}
	}

	@Suppress("unchecked_cast")
	fun handleBooleanProperty(property: SlimePropertiesEnum, value: Any, properties: SlimePropertyMap, mode: Changer.ChangeMode) {
		if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) return
		try {
			val prop = property.prop as SlimeProperty<Boolean>
			val convertedValue = Util.anyToBoolean(value) ?: run {
				Skript.error("Expected a Boolean value for property ${property.name} but got null instead!")
				return
			}
			properties.setValue(prop, convertedValue)
		} catch (e: NumberFormatException) {
			Skript.error("Expected a Boolean value for property ${property.name}")
		}
	}
}