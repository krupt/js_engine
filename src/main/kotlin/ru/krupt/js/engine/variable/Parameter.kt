package ru.krupt.js.engine.variable

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

data class Parameter(
        @get:[NotBlank ApiModelProperty("Имя параметра", required = true)]
        val name: String,
        @ApiModelProperty("Тип параметра", required = true)
        val type: DataType,
        @ApiModelProperty("Является ли параметр обязательным. Значение по-умолчанию: true")
        val required: Boolean = true
) {

    override fun toString(): String {
        return "$name: $type" + (if (!required) "?" else "")
    }
}
