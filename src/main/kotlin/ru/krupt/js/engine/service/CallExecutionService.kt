package ru.krupt.js.engine.service

import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import ru.krupt.js.engine.util.getOrConvertValue
import ru.krupt.js.engine.variable.Parameter

@Service
class CallExecutionService(
        val callStorageService: CallStorageService,
        val javaScriptExecutorService: JavaScriptExecutorService) {

    fun call(callName: String, inputs: Map<String, Any?>): Map<String, Any?> {
        val callEntity = callStorageService.getCall(callName)
        val convertedInputs = validateAndConvertInputs(callEntity.invocationInfo.inputs, inputs)

        return javaScriptExecutorService.execute(callEntity.body, convertedInputs)
    }

    private fun validateAndConvertInputs(declaredInputs: Set<Parameter>, inputs: Map<String, Any?>):
            Map<String, Any?> {
        val convertedInputs = HashMap<String, Any?>()
        for (variable in declaredInputs) {
            val value = inputs[variable.name]
            if (variable.required) {
                if (value == null) {
                    throw IllegalArgumentException("Required input parameter '${variable.name}'" +
                            " not passed")
                } else if (value is String && !StringUtils.hasText(value)) {
                    throw IllegalArgumentException("Input parameter '${variable.name}' " +
                            "couldn't be blank")
                }
            }
            try {
                convertedInputs[variable.name] = variable.getOrConvertValue(value)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid value '$value' for input parameter " +
                        "'${variable.name}: ${variable.type}'", e)
            }
        }
        return convertedInputs
    }
}
