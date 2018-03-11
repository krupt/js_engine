package ru.krupt.demo.variable

import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.startsWith
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeParseException

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [VariableConversionService::class])
class VariableConversionServiceTest {

    @Autowired
    private lateinit var variableConversionService: VariableConversionService

    @Test
    fun testSimple() {
        assertNull(variableConversionService.getValueForVariableType(DataType.TEXT, null))
        assertTrue(variableConversionService.getValueForVariableType(
                DataType.BOOLEAN, true) as Boolean)
        assertFalse(variableConversionService.getValueForVariableType(
                DataType.BOOLEAN, false) as Boolean)
        assertEquals(12334, variableConversionService.getValueForVariableType(
                DataType.INTEGER, 12334))
        assertEquals(14333232334, variableConversionService.getValueForVariableType(
                DataType.INTEGER, 14333232334))
        assertEquals(123.1F, variableConversionService.getValueForVariableType(
                DataType.DECIMAL, 123.1F))
        assertEquals(123.1, variableConversionService.getValueForVariableType(
                DataType.DECIMAL, 123.1))
        assertEquals(123, variableConversionService.getValueForVariableType(
                DataType.DECIMAL, 123))
        assertEquals(14333232334, variableConversionService.getValueForVariableType(
                DataType.DECIMAL, 14333232334))
        assertEquals("Hello", variableConversionService.getValueForVariableType(
                DataType.TEXT, "Hello"))
    }

    @Test
    fun testWithConversion() {
        assertTrue(variableConversionService.getValueForVariableType(
                DataType.BOOLEAN, "true") as Boolean)
        assertFalse(variableConversionService.getValueForVariableType(
                DataType.BOOLEAN, "false") as Boolean)
        assertEquals(12334L, variableConversionService.getValueForVariableType(
                DataType.INTEGER, "12334"))
        assertEquals(123.1, variableConversionService.getValueForVariableType(
                DataType.DECIMAL, "123.1"))
        assertEquals(12332.0, variableConversionService.getValueForVariableType(
                DataType.DECIMAL, "12332"))
        assertEquals(LocalDate.of(2018, Month.MARCH, 9),
                variableConversionService.getValueForVariableType(
                        DataType.DATE, "2018-03-09"))
        assertEquals(LocalDateTime.of(2018, Month.MARCH, 10, 11, 1, 53),
                variableConversionService.getValueForVariableType(
                        DataType.DATE_TIME, "2018-03-10T11:01:53"))
    }

    @Test
    fun testInvalidFormat() {
        try {
            variableConversionService.getValueForVariableType(DataType.BOOLEAN, "adsdasasd1")
        } catch (e: Exception) {
            assertThat(e, instanceOf(IllegalArgumentException::class.java))
            assertEquals("Invalid boolean value 'adsdasasd1'", e.message)
        }
        try {
            variableConversionService.getValueForVariableType(
                    DataType.INTEGER, "12334.31")
        } catch (e: Exception) {
            assertThat(e, instanceOf(NumberFormatException::class.java))
            assertEquals("For input string: \"12334.31\"", e.message)
        }
        try {
            variableConversionService.getValueForVariableType(
                    DataType.INTEGER, "12334as")
        } catch (e: Exception) {
            assertThat(e, instanceOf(NumberFormatException::class.java))
            assertEquals("For input string: \"12334as\"", e.message)
        }
        try {
            variableConversionService.getValueForVariableType(
                    DataType.DECIMAL, "12334as")
        } catch (e: Exception) {
            assertThat(e, instanceOf(NumberFormatException::class.java))
            assertEquals("For input string: \"12334as\"", e.message)
        }
        try {
            variableConversionService.getValueForVariableType(
                    DataType.DATE, "2018-02-29")
        } catch (e: Exception) {
            assertThat(e, instanceOf(DateTimeParseException::class.java))
            assertThat(e.message, startsWith("Text '2018-02-29' could not be parsed"))
        }
        try {
            variableConversionService.getValueForVariableType(
                    DataType.DATE_TIME, "2018-02-29T11:01:53")
        } catch (e: Exception) {
            assertThat(e, instanceOf(DateTimeParseException::class.java))
            assertThat(e.message, startsWith("Text '2018-02-29T11:01:53' could not be parsed"))
        }
        try {
            variableConversionService.getValueForVariableType(
                    DataType.DATE_TIME, "2018-02-28T24:01:53")
        } catch (e: Exception) {
            assertThat(e, instanceOf(DateTimeParseException::class.java))
            assertThat(e.message, startsWith("Text '2018-02-28T24:01:53' could not be parsed"))
        }
        try {
            variableConversionService.getValueForVariableType(DataType.INTEGER, 123.12)
        } catch (e: Exception) {
            assertThat(e, instanceOf(IllegalArgumentException::class.java))
            assertEquals("Value '123.12' could not be converted to dataType 'INTEGER'",
                    e.message)
        }
    }
}
