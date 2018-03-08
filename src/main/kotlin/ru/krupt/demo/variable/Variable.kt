package ru.krupt.demo.variable

data class Variable(
        val name: String,
        val required: Boolean = true,
        val emptyAllowed: Boolean = false,
        val type: DataType
) {

    init {
        if (!emptyAllowed && !required) {
            throw IllegalArgumentException("Empty allowed check can't be enabled " +
                    "for non-required variables")
        }
    }

    override fun toString(): String {
        return "$name: $type" + (if (!required) "?" else "")
    }
}
