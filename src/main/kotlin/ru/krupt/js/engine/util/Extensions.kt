package ru.krupt.js.engine.util

import ru.krupt.js.engine.variable.DataType
import ru.krupt.js.engine.variable.Parameter
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Unwrap java.util.Optional to Kotlin nullable object
 */
fun <T> Optional<T>.unwrap(): T? = orElse(null)

/**
 * Get a value for a variable based on the type of variable
 */
fun Parameter.getOrConvertValue(value: Any?) = getOrConvertValueForVariableType(type, value)

private fun getOrConvertValueForVariableType(dataType: DataType, value: Any?): Any? {
    if (value == null) {
        return null
    }
    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    when (dataType) {
        DataType.BOOLEAN -> {
            if (value is Boolean) {
                return value
            } else if (value is String) {
                return when (value) {
                    "true" -> true
                    "false" -> false
                    else -> throw IllegalArgumentException("Invalid boolean value '$value'")
                }
            }
        }
        DataType.INTEGER -> {
            if (value is Int || value is Long) {
                return value
            } else if (value is String) {
                return value.toLong()
            }
        }
        DataType.DECIMAL -> {
            if (value is Float || value is Double || value is Int || value is Long) {
                return value
            } else if (value is String) {
                return value.toDouble()
            }
        }
        DataType.TEXT -> {
            if (value is String) {
                return value
            }
        }
        DataType.DATE -> {
            if (value is String) {
                return LocalDate.parse(value)
            }
        }
        DataType.DATE_TIME -> {
            if (value is String) {
                return LocalDateTime.parse(value)
            }
        }
        else -> throw IllegalStateException("Converse for dataType '$dataType' not implemented")
    }
    throw IllegalArgumentException("Value '$value' could not be converted to dataType '$dataType'")
}
