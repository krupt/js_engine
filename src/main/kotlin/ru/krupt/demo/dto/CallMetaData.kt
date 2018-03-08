package ru.krupt.demo.dto

import ru.krupt.demo.variable.InvocationInfo

data class CallMetaData(
        val description: String,
        val invocationInfo: InvocationInfo,
        val tests: Collection<TestMetaData>)

data class TestMetaData(
        val name: String,
        val inputs: Map<String, Any>,
        val outputs: Map<String, Any>)
