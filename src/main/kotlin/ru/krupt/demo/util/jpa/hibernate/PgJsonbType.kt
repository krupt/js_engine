package ru.krupt.demo.util.jpa.hibernate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.ParameterizedType
import org.hibernate.usertype.UserType
import org.postgresql.util.PGobject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.util.*

class PgJsonbType : UserType, ParameterizedType {

    companion object {
        const val TYPE_CLASS_PROPERTY_NAME: String = "TypeClass"
        @JvmStatic
        private val MAPPER: ObjectMapper = ObjectMapper()
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(PgJsonbType::class.java)

        init {
            MAPPER.registerKotlinModule()
        }
    }

    private lateinit var typeClass: Class<*>

    override fun setParameterValues(parameters: Properties?) {
        typeClass = Class.forName(parameters?.getProperty(TYPE_CLASS_PROPERTY_NAME))
    }

    override fun sqlTypes() = intArrayOf(Types.JAVA_OBJECT)

    override fun returnedClass() = typeClass

    override fun equals(x: Any?, y: Any?) =
            if (x == null)
                y == null
            else
                x == y

    override fun hashCode(x: Any?) = x?.hashCode() ?: 0

    override fun nullSafeGet(resultSet: ResultSet,
                             names: Array<out String>,
                             session: SharedSessionContractImplementor,
                             owner: Any?): Any? {
        log.trace("NullSafeGet {} from {}", names[0], owner)
        if (resultSet.getObject(names[0]) == null) {
            return null
        }
        val rawValue = resultSet.getString(names[0])
        var jsonObject: Any? = null
        try {
            jsonObject = MAPPER.readValue(rawValue, returnedClass())
        } catch (e: IOException) {
            log.error("Can't deserialize {} from '{}'", returnedClass(), rawValue, e)
        }

        return jsonObject
    }

    override fun nullSafeSet(preparedStatement: PreparedStatement,
                             value: Any?,
                             index: Int,
                             session: SharedSessionContractImplementor) {
        log.trace("NullSafeSet {}", value)
        if (value == null) {
            preparedStatement.setNull(index, Types.NULL)
            return
        }
        var jsonString: String? = null
        try {
            jsonString = MAPPER.writeValueAsString(value)
        } catch (e: IOException) {
            log.error("Can't serialize '{}' to {}", value, returnedClass(), e)
        }

        val pgObject = PGobject()
        pgObject.type = "jsonb"
        pgObject.value = jsonString
        preparedStatement.setObject(index, pgObject)
    }

    override fun deepCopy(value: Any?): Any? {
        log.trace("DeepCopy {}", value)
        if (value != null) {
            var jsonValue: String? = null
            try {
                jsonValue = MAPPER.writeValueAsString(value)
                return MAPPER.readValue(jsonValue, returnedClass())
            } catch (e: IOException) {
                log.error("Can't complete deepCopy {} as '{}' to {}", value,
                        jsonValue, returnedClass(), e)
            }
        }
        return null
    }

    override fun isMutable() = false

    override fun disassemble(value: Any?): Serializable? {
        log.trace("Disassemble {}", value)
        return MAPPER.writeValueAsString(value)
    }

    override fun assemble(cached: Serializable?, owner: Any?): Any? {
        log.trace("Assemble {}", cached)
        return MAPPER.readValue(cached as String, returnedClass())
    }

    override fun replace(original: Any?, target: Any?, owner: Any?): Any? {
        return deepCopy(original)
    }
}
