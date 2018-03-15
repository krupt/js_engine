package ru.krupt.js.engine.service

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import ru.krupt.js.engine.TestJacksonConfiguration
import ru.krupt.js.engine.domain.CallEntity
import ru.krupt.js.engine.repository.CallRepository
import ru.krupt.js.engine.variable.DataType
import ru.krupt.js.engine.variable.InvocationInfo
import ru.krupt.js.engine.variable.Parameter
import javax.validation.Validator

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestJacksonConfiguration::class, CallStorageService::class])
@MockBean(Validator::class)
class CallStorageServiceTest {

    @Autowired
    lateinit var callStorageService: CallStorageService

    @MockBean
    lateinit var callRepository: CallRepository

    @Test
    fun testSuccessfulSaveNewCall() {
        val inputStream = Mockito.spy(
                this.javaClass.getResourceAsStream("/call/simple_case.js"))

        BDDMockito.given(callRepository.save(any(CallEntity::class.java)))
                .will({ (it.getArgument(0) as CallEntity).copy(id = 1) })

        val (id, name, description, invocationInfo, body, version) =
                callStorageService.parseInputStreamAndSaveCall(inputStream, "simple_case")

        assertEquals(1, id)
        assertEquals("simple_case", name)
        assertEquals("Этот колл проверяет существование клиента", description)

        val expectedInvocationInfo = InvocationInfo(
                setOf(Parameter(name = "CLIENT_ID", type = DataType.TEXT)),
                setOf(Parameter(name = "EXISTS", type = DataType.BOOLEAN)))
        assertEquals(expectedInvocationInfo, invocationInfo)

        val expectedBody = this.javaClass.getResourceAsStream("/call/simple_case.js")
                .bufferedReader().use { it.readText() }
        assertEquals(expectedBody, body)
        assertEquals(0, version)

        Mockito.verify(inputStream).close()
    }

    @Test
    fun testSuccessfulSaveNewCallWithoutInputs() {
        val inputStream = Mockito.spy(
                this.javaClass.getResourceAsStream("/call/get_date.js"))

        BDDMockito.given(callRepository.save(any(CallEntity::class.java)))
                .will({ (it.getArgument(0) as CallEntity).copy(id = 1) })

        val (id, name, description, invocationInfo, body, version) =
                callStorageService.parseInputStreamAndSaveCall(inputStream, "get_date")

        assertEquals(1, id)
        assertEquals("get_date", name)
        assertEquals("Возвращает текущую дату", description)

        val expectedInvocationInfo = InvocationInfo(
                outputs = setOf(Parameter(name = "CURRENT_DATE", type = DataType.DATE)))
        assertEquals(expectedInvocationInfo, invocationInfo)

        val expectedBody = this.javaClass.getResourceAsStream("/call/get_date.js")
                .bufferedReader().use { it.readText() }
        assertEquals(expectedBody, body)
        assertEquals(0, version)

        Mockito.verify(inputStream).close()
    }
}
