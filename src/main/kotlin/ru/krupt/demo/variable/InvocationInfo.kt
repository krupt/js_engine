package ru.krupt.demo.variable

import javax.validation.Valid
import javax.validation.constraints.Size

data class InvocationInfo(
        @get:Valid
        val inputs: Set<Variable> = emptySet(),
        @get:[Size(min = 1) Valid]
        val outputs: Set<Variable>
)
