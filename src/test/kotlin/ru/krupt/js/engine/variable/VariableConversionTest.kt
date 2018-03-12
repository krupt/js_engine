package ru.krupt.js.engine.variable

import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.startsWith
import org.junit.Assert.*
import org.junit.Test
import ru.krupt.js.engine.util.getOrConvertValue
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeParseException

class VariableConversionTest {

    @Test
    fun testSimple() {
        assertNull(Variable("", DataType.TEXT).getOrConvertValue(null))
        assertTrue(Variable("", DataType.BOOLEAN).getOrConvertValue(true) as Boolean)
        assertFalse(Variable("", DataType.BOOLEAN).getOrConvertValue(false) as Boolean)
        assertEquals(12334, Variable("", DataType.INTEGER)
                .getOrConvertValue(12334))
        assertEquals(14333232334, Variable("", DataType.INTEGER)
                .getOrConvertValue(14333232334))
        assertEquals(123.1F, Variable("", DataType.DECIMAL)
                .getOrConvertValue(123.1F))
        assertEquals(123.1, Variable("", DataType.DECIMAL)
                .getOrConvertValue(123.1))
        assertEquals(123, Variable("", DataType.DECIMAL)
                .getOrConvertValue(123))
        assertEquals(14333232334, Variable("", DataType.DECIMAL)
                .getOrConvertValue(14333232334))
        assertEquals("Hello", Variable("", DataType.TEXT)
                .getOrConvertValue("Hello"))
    }

    @Test
    fun testWithConversion() {
        assertTrue(Variable("", DataType.BOOLEAN).getOrConvertValue("true") as Boolean)
        assertFalse(Variable("", DataType.BOOLEAN).getOrConvertValue("false") as Boolean)
        assertEquals(12334L, Variable("", DataType.INTEGER)
                .getOrConvertValue("12334"))
        assertEquals(123.1, Variable("", DataType.DECIMAL)
                .getOrConvertValue("123.1"))
        assertEquals(12332.0, Variable("", DataType.DECIMAL)
                .getOrConvertValue("12332"))
        assertEquals(LocalDate.of(2018, Month.MARCH, 9),
                Variable("", DataType.DATE).getOrConvertValue("2018-03-09"))
        assertEquals(LocalDateTime.of(2018, Month.MARCH, 10, 11, 1, 53),
                Variable("", DataType.DATE_TIME)
                        .getOrConvertValue("2018-03-10T11:01:53"))
    }

    @Test
    fun testInvalidFormat() {
        try {
            Variable("", DataType.BOOLEAN).getOrConvertValue("adsdasasd1")
        } catch (e: Exception) {
            assertThat(e, instanceOf(IllegalArgumentException::class.java))
            assertEquals("Invalid boolean value 'adsdasasd1'", e.message)
        }
        try {
            Variable("", DataType.INTEGER).getOrConvertValue("12334.31")
        } catch (e: Exception) {
            assertThat(e, instanceOf(NumberFormatException::class.java))
            assertEquals("For input string: \"12334.31\"", e.message)
        }
        try {
            Variable("", DataType.INTEGER).getOrConvertValue("12334as")
        } catch (e: Exception) {
            assertThat(e, instanceOf(NumberFormatException::class.java))
            assertEquals("For input string: \"12334as\"", e.message)
        }
        try {
            Variable("", DataType.DECIMAL).getOrConvertValue("12334as")
        } catch (e: Exception) {
            assertThat(e, instanceOf(NumberFormatException::class.java))
            assertEquals("For input string: \"12334as\"", e.message)
        }
        try {
            Variable("", DataType.DATE).getOrConvertValue("2018-02-29")
        } catch (e: Exception) {
            assertThat(e, instanceOf(DateTimeParseException::class.java))
            assertThat(e.message, startsWith("Text '2018-02-29' could not be parsed"))
        }
        try {
            Variable("", DataType.DATE_TIME).getOrConvertValue("2018-02-29T11:01:53")
        } catch (e: Exception) {
            assertThat(e, instanceOf(DateTimeParseException::class.java))
            assertThat(e.message, startsWith("Text '2018-02-29T11:01:53' could not be parsed"))
        }
        try {
            Variable("", DataType.DATE_TIME).getOrConvertValue("2018-02-28T24:01:53")
        } catch (e: Exception) {
            assertThat(e, instanceOf(DateTimeParseException::class.java))
            assertThat(e.message, startsWith("Text '2018-02-28T24:01:53' could not be parsed"))
        }
        try {
            Variable("", DataType.INTEGER).getOrConvertValue(123.12)
        } catch (e: Exception) {
            assertThat(e, instanceOf(IllegalArgumentException::class.java))
            assertEquals("Value '123.12' could not be converted to dataType 'INTEGER'",
                    e.message)
        }
    }
}
