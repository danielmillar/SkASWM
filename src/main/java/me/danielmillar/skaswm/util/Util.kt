package me.danielmillar.skaswm.util

object Util {

	fun anyToInt(value: Any): Int? {
		return when (value) {
			is Int -> value

			is Long -> if (value in Int.MIN_VALUE..Int.MAX_VALUE) value.toInt() else null

			is String -> value.toIntOrNull()

			is Double -> if (value in Int.MIN_VALUE.toDouble()..Int.MAX_VALUE.toDouble()) value.toInt() else null
			is Float -> if (value in Int.MIN_VALUE.toFloat()..Int.MAX_VALUE.toFloat()) value.toInt() else null

			is Boolean -> if (value) 1 else 0

			is Byte -> value.toInt()
			is Short -> value.toInt()

			is Char -> value.code

			// For unsupported types, return null
			else -> null
		}
	}

	fun anyToFloat(value: Any): Float? {
		return when (value) {
			is Float -> value

			is Int -> value.toFloat()
			is Long -> value.toFloat()

			is String -> value.toFloatOrNull()

			is Double -> value.toFloat()

			is Boolean -> if (value) 1.0f else 0.0f

			is Byte -> value.toFloat()
			is Short -> value.toFloat()

			is Char -> value.code.toFloat()

			// For unsupported types, return null
			else -> null
		}
	}

	fun anyToBoolean(value: Any): Boolean? {
		return when (value) {
			// Directly return if it's already a Boolean
			is Boolean -> value

			// Handle Int, Long, Float, Double
			is Int -> value != 0
			is Long -> value != 0L
			is Float -> value != 0.0f
			is Double -> value != 0.0

			// Handle String, common interpretations of true/false
			is String -> value.equals("true", ignoreCase = true) || value == "1"

			// For unsupported types, return null
			else -> null
		}
	}

}