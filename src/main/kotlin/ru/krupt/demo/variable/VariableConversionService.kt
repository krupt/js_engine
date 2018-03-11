package ru.krupt.demo.variable

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class VariableConversionService {

    fun getValueForVariableType(dataType: DataType, value: Any?): Any? {
        if (value == null) {
            return null
        }
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
}
