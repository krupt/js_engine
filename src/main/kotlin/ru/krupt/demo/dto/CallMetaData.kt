package ru.krupt.demo.dto

import ru.krupt.demo.variable.InvocationInfo
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CallMetaData(
        @get:NotBlank
        val description: String,
        @get:Valid
        val invocationInfo: InvocationInfo,
        @get:[Size(min = 1) Valid]
        val tests: Collection<TestMetaData>)

data class TestMetaData(
        @get:NotBlank
        val name: String,
        val inputs: Map<String, Any?> = emptyMap(),
        @get:Size(min = 1)
        val outputs: Map<String, Any?>)
