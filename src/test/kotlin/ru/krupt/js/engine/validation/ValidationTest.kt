package ru.krupt.js.engine.validation

import org.junit.Test
import ru.krupt.js.engine.dto.CallMetaData
import ru.krupt.js.engine.dto.TestMetaData
import ru.krupt.js.engine.variable.DataType
import ru.krupt.js.engine.variable.InvocationInfo
import ru.krupt.js.engine.variable.Variable
import javax.validation.Validation

class ValidationTest {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun testInvalidMeta() {
        val callMetaData = CallMetaData("",
                InvocationInfo(outputs = setOf(Variable("", type = DataType.TEXT),
                        Variable("", type = DataType.INTEGER))),
                listOf(TestMetaData("", outputs = mapOf(Pair("", null))))
        )
        val errors = validator.validate(callMetaData)
        errors.map { "${it.propertyPath} ${it.message}" }
                .forEach({ println(it) })
    }
}
