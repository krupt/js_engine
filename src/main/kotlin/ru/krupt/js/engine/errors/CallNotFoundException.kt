package ru.krupt.js.engine.errors

class CallNotFoundException(callName: String) : RuntimeException("Call '$callName' not found")
