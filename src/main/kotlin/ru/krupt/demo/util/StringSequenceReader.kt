package ru.krupt.demo.util

import java.io.Reader

class StringSequenceReader(
        val first: String,
        val second: Reader
) : Reader() {

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        first.toCharArray(cbuf)
        val length = first.length
        return length + second.read(cbuf, length, len - length)
    }

    override fun close() {
        second.close()
    }
}
