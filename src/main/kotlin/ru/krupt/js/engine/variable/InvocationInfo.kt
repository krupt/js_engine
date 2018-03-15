package ru.krupt.js.engine.variable

import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.Size

data class InvocationInfo(
        @get:[Valid ApiModelProperty]
        val inputs: Set<Parameter> = emptySet(),
        @get:[Size(min = 1) Valid]
        val outputs: Set<Parameter>
)
