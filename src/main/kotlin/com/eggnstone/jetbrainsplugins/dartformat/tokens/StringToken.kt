package com.eggnstone.jetbrainsplugins.dartformat.tokens

import com.eggnstone.jetbrainsplugins.dartformat.Tools

class StringToken(val text: String, val isClosed: Boolean = true) : IToken
{
    override fun equals(other: Any?): Boolean = other is StringToken && text == other.text

    override fun hashCode(): Int = text.hashCode()

    override fun recreate(): String = text

    override fun toString(): String = "String(\"${Tools.toDisplayString(text)}${if (isClosed) "\"" else "\", isClosed=false"})"
}
