package ru.krupt.demo.errors

class CallNotFoundException(callName: String) : RuntimeException("Call '$callName' not found")
