package ru.krupt.js.engine.variable

import javax.validation.constraints.NotBlank

data class Variable(
        @get:NotBlank
        val name: String,
        val type: DataType,
        val required: Boolean = true
) {

    override fun toString(): String {
        return "$name: $type" + (if (!required) "?" else "")
    }
}
