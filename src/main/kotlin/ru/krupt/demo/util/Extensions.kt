package ru.krupt.demo.util

import java.util.*

fun <T> Optional<T>.unwrap(): T? = orElse(null)
