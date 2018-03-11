package ru.krupt.demo.validation

import org.junit.Test
import ru.krupt.demo.dto.CallMetaData
import ru.krupt.demo.dto.TestMetaData
import ru.krupt.demo.variable.DataType
import ru.krupt.demo.variable.InvocationInfo
import ru.krupt.demo.variable.Variable
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
