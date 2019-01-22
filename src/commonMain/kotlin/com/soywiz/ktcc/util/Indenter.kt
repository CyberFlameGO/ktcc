package com.soywiz.ktcc.util

inline fun Indenter(callback: Indenter.() -> Unit): String = Indenter().apply(callback).toString()

class Indenter {
    @PublishedApi
    internal val cmds = arrayListOf<Any>()

    @PublishedApi
    internal object Indent
    @PublishedApi
    internal object Unindent

    fun line(str: String) {
        cmds += str
    }

    inline fun line(str: String, callback: () -> Unit) {
        line("$str {")
        indent {
            callback()
        }
        line("}")
    }

    inline fun indent() = run { cmds += Indent }
    inline fun unindent() = run { cmds += Unindent }

    inline fun <T> indent(callback: () -> T): T {
        indent()
        try {
            return callback()
        } finally {
            unindent()
        }
    }

    object Indents {
        val indents = Array(1024) { "" }.apply {
            val builder = StringBuilder()
            for (n in 0 until 1024) {
                this[n] = builder.toString()
                builder.append('\n')
            }
        }

        operator fun get(index: Int): String = indents.getOrNull(index) ?: error("Too much indentation ($index)")
    }

    override fun toString(): String = buildString {
        var indent = 0
        for (cmd in cmds) {
            when (cmd) {
                Indent -> indent++
                Unindent -> indent--
                is String -> {
                    for (line in cmd.split("\n")) {
                        append(Indents[indent])
                        append("$line\n")
                    }
                }
            }
        }
    }
}