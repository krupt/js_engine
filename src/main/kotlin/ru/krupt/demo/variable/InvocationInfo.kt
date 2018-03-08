package ru.krupt.demo.variable

data class InvocationInfo(
        val inputs: Set<Variable>? = null,
        val outputs: Set<Variable>
)
