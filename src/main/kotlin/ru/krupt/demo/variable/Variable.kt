package ru.krupt.demo.variable

import javax.validation.constraints.NotBlank

data class Variable(
        @get:NotBlank
        val name: String,
        val required: Boolean = true,
        val type: DataType
) {

    override fun toString(): String {
        return "$name: $type" + (if (!required) "?" else "")
    }
}
