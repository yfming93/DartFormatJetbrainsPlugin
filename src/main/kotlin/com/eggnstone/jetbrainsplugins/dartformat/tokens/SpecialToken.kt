package com.eggnstone.jetbrainsplugins.dartformat.tokens

import com.eggnstone.jetbrainsplugins.dartformat.Constants
import com.eggnstone.jetbrainsplugins.dartformat.Tools

class SpecialToken(val text: String) : IToken
{
    companion object
    {
        val OPENING_ANGLE_BRACKET = SpecialToken(Constants.OPENING_ANGLE_BRACKET)
        val CLOSING_ANGLE_BRACKET = SpecialToken(Constants.CLOSING_ANGLE_BRACKET)

        val OPENING_CURLY_BRACKET = SpecialToken(Constants.OPENING_CURLY_BRACKET)
        val CLOSING_CURLY_BRACKET = SpecialToken(Constants.CLOSING_CURLY_BRACKET)

        val OPENING_ROUND_BRACKET = SpecialToken(Constants.OPENING_ROUND_BRACKET)
        val CLOSING_ROUND_BRACKET = SpecialToken(Constants.CLOSING_ROUND_BRACKET)

        val OPENING_SQUARE_BRACKET = SpecialToken(Constants.OPENING_SQUARE_BRACKET)
        val CLOSING_SQUARE_BRACKET = SpecialToken(Constants.CLOSING_SQUARE_BRACKET)

        val ARROW = SpecialToken(Constants.ARROW)
        val COLON = SpecialToken(Constants.COLON)
        val COMMA = SpecialToken(Constants.COMMA)
        val EQUAL = SpecialToken(Constants.EQUAL)
        val PERIOD = SpecialToken(Constants.PERIOD)
        val SEMICOLON = SpecialToken(Constants.SEMICOLON)
    }

    val isClosingBracket get() = this == CLOSING_CURLY_BRACKET || this == CLOSING_ANGLE_BRACKET || this == CLOSING_ROUND_BRACKET || this == CLOSING_SQUARE_BRACKET

    val isOpeningBracket get() = this == OPENING_CURLY_BRACKET || this == OPENING_ANGLE_BRACKET || this == OPENING_ROUND_BRACKET || this == OPENING_SQUARE_BRACKET

    override fun equals(other: Any?): Boolean = other is SpecialToken && text == other.text

    override fun hashCode(): Int = text.hashCode()

    override fun recreate(): String = text

    override fun toString(): String = "Special(\"${Tools.toDisplayString(text)}\")"
}
