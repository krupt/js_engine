package ru.krupt.demo.service

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import ru.krupt.demo.domain.CallEntity
import ru.krupt.demo.dto.CallFullInfoDto
import ru.krupt.demo.dto.CallMetaData
import ru.krupt.demo.errors.CallNotFoundException
import ru.krupt.demo.repository.CallRepository
import ru.krupt.demo.util.unwrap
import ru.krupt.demo.variable.Variable
import ru.krupt.demo.variable.VariableConversionService
import java.io.IOException
import java.io.InputStream
import javax.validation.Validator

@Service
class CallService(
        val callRepository: CallRepository,
        val validator: Validator,
        val variableConversionService: VariableConversionService,
        val javaScriptExecutorService: JavaScriptExecutorService,
        objectMapper: ObjectMapper) {
    private final val javaScriptObjectMapper: ObjectMapper = objectMapper.copy()

    @Autowired
    private lateinit var self: CallService

    init {
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS)
    }

    fun getCallInvocationInfo(callName: String) =
            callRepository.findInvocationInfoByNameIgnoreCase(callName)?.getInvocationInfo()
                    ?: throw CallNotFoundException(callName)

    fun parseInputStreamAndSaveCall(inputStream: InputStream, callName: String): CallFullInfoDto {
        val callBody: String = try {
            inputStream.bufferedReader()
                    .use { it.readText() }
        } catch (e: IOException) {
            throw RuntimeException("Error occurred when reading call's body", e)
        }
        val metaDataRegex = Regex("""meta\s*=\s*\{""")
        val matchResult = metaDataRegex.find(callBody)
        val metaInfoStartIndex = matchResult?.groups?.get(0)?.range?.endInclusive
                ?: throw IllegalArgumentException("Couldn't find meta")

        val callMetaData: CallMetaData = try {
            javaScriptObjectMapper.readValue(callBody.substring(metaInfoStartIndex),
                    CallMetaData::class.java)
        } catch (e: IOException) {
            throw IllegalArgumentException("Invalid format of call's meta information", e)
        }

        val errors = validator.validate(callMetaData)

        if (!errors.isEmpty()) {
            throw IllegalArgumentException(errors.joinToString(",\n",
                    "Call's meta validation failed: \n") { "${it.propertyPath} ${it.message}" })
        }

        return CallFullInfoDto.fromEntity(self.createOrUpdateCall(callName, callMetaData, callBody))
    }

    // Do it in one transaction to avoid detach object (and executing two select queries)
    @Transactional
    fun createOrUpdateCall(callName: String,
                           callMetaData: CallMetaData,
                           callBody: String): CallEntity {
        var callEntity: CallEntity? = callRepository.findOneByNameIgnoreCase(callName)
        callEntity = if (callEntity != null) {
            callEntity.copy(description = callMetaData.description,
                    invocationInfo = callMetaData.invocationInfo,
                    body = callBody)
        } else {
            CallEntity(name = callName, description = callMetaData.description,
                    invocationInfo = callMetaData.invocationInfo,
                    body = callBody)
        }
        return callRepository.save(callEntity)
    }

    fun getCall(callName: String) = CallFullInfoDto.fromEntity(
            callRepository.findOneByNameIgnoreCase(callName)
                    ?: throw CallNotFoundException(callName))

    fun getCallById(callId: Long) = CallFullInfoDto.fromEntity(
            callRepository.findById(callId).unwrap()
                    ?: throw CallNotFoundException(callId.toString()))

    fun call(callName: String, inputs: Map<String, Any?>): Map<String, Any?> {
        val callEntity = callRepository.findOneByNameIgnoreCase(callName)
                ?: throw CallNotFoundException(callName)

        val convertedInputs = validateAndConvertInputs(callEntity.invocationInfo.inputs, inputs)

        return javaScriptExecutorService.execute(callEntity.body, convertedInputs)
    }

    private fun validateAndConvertInputs(declaredInputs: Set<Variable>, inputs: Map<String, Any?>):
            Map<String, Any?> {
        val convertedInputs = HashMap<String, Any?>()
        for (variable in declaredInputs) {
            val value = inputs[variable.name]
            if (variable.required) {
                if (value == null) {
                    throw IllegalArgumentException("Required input parameter '${variable.name}' not " +
                            "passed")
                } else if (value is String && !StringUtils.hasText(value)) {
                    throw IllegalArgumentException("Input parameter '${variable.name}' " +
                            "couldn't be blank")
                }
            }
            try {
                convertedInputs[variable.name] = variableConversionService
                        .getValueForVariableType(variable.type, value)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid value '$value' for input parameter " +
                        "'${variable.name}: ${variable.type}'", e)
            }
        }
        return convertedInputs
    }
}
