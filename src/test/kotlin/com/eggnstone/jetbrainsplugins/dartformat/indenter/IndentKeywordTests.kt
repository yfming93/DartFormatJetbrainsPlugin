package com.eggnstone.jetbrainsplugins.dartformat.indenter

import com.eggnstone.jetbrainsplugins.dartformat.tokens.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Test

class IndentKeywordTests
{
    @Test
    fun keywordAndExpression_atLineStart()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "), SpecialToken("("), UnknownToken("a"), WhiteSpaceToken(" "), SpecialToken("=="), WhiteSpaceToken(" "), UnknownToken("b"), SpecialToken(")"), LineBreakToken("\n"),
            UnknownToken("abc"), SpecialToken(";")
        )
        val expectedOutputText =
            "if (a == b)\n" +
            "    abc;"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }

    @Test
    fun keywordAndExpressionWithRoundBrackets_atLineStart()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "), SpecialToken("("), UnknownToken("a"), WhiteSpaceToken(" "), SpecialToken("=="), WhiteSpaceToken(" "), UnknownToken("b"), SpecialToken(")"), LineBreakToken("\n"),
            UnknownToken("abc"), SpecialToken("("), SpecialToken(")"), SpecialToken(";")
        )
        val expectedOutputText =
            "if (a == b)\n" +
            "    abc();"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }

    @Test
    fun keywordAndExpression_IndentationClearedAfterwards()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "), SpecialToken("("), UnknownToken("a"), WhiteSpaceToken(" "), SpecialToken("=="), WhiteSpaceToken(" "), UnknownToken("b"), SpecialToken(")"), LineBreakToken("\n"),
            UnknownToken("abc"), SpecialToken(";"), LineBreakToken("\n"),
            UnknownToken("def"), SpecialToken(";")
        )
        val expectedOutputText =
            "if (a == b)\n" +
            "    abc;\n" +
            "def;"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }

    @Test
    fun keywordAndExpressionOnSameLine_IndentationClearedAfterwards()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "), SpecialToken("("), UnknownToken("a"), WhiteSpaceToken(" "), SpecialToken("=="), WhiteSpaceToken(" "), UnknownToken("b"), SpecialToken(")"), WhiteSpaceToken(" "), UnknownToken("abc"), SpecialToken(";"), LineBreakToken("\n"),
            UnknownToken("def"), SpecialToken(";")
        )
        val expectedOutputText =
            "if (a == b) abc;\n" +
            "def;"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }

    @Test
    fun keywordAndExpressionWithRoundBrackets_IndentationClearedAfterwards()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "), SpecialToken("("), UnknownToken("a"), WhiteSpaceToken(" "), SpecialToken("=="), WhiteSpaceToken(" "), UnknownToken("b"), SpecialToken(")"), LineBreakToken("\n"),
            UnknownToken("abc"), SpecialToken("("), SpecialToken(")"), SpecialToken(";"), LineBreakToken("\n"),
            UnknownToken("def"), SpecialToken(";")
        )
        val expectedOutputText =
            "if (a == b)\n" +
            "    abc();\n" +
            "def;"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }

    @Test
    fun keywordAndExpressionWithRoundBracketsOnSameLine_IndentationClearedAfterwards()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "), SpecialToken("("), UnknownToken("a"), WhiteSpaceToken(" "), SpecialToken("=="), WhiteSpaceToken(" "), UnknownToken("b"), SpecialToken(")"), WhiteSpaceToken(" "), UnknownToken("abc"), SpecialToken("("), SpecialToken(")"), SpecialToken(";"), LineBreakToken("\n"),
            UnknownToken("def"), SpecialToken(";")
        )
        val expectedOutputText =
            "if (a == b) abc();\n" +
            "def;"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }

    @Test
    fun keywordWithOpeningAndClosingCurlyBracketInSameLine()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "),
            SpecialToken("("), UnknownToken("a"), WhiteSpaceToken(" "), SpecialToken("=="), WhiteSpaceToken(" "), UnknownToken("b"), SpecialToken(")"),
            WhiteSpaceToken(" "), SpecialToken("{"), SpecialToken("}"), LineBreakToken("\n"),
            UnknownToken("abc"), SpecialToken(";")
        )
        val expectedOutputText =
            "if (a == b) {}\n" +
            "abc;"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }

    @Test
    fun keyword_openingBracketOnNextLineReplacesKeywordInStack()
    {
        val inputTokens = arrayListOf(
            KeywordToken("if"), WhiteSpaceToken(" "), SpecialToken("("), SpecialToken(")"), LineBreakToken("\n"),
            UnknownToken("abc"), SpecialToken("("), LineBreakToken("\n")
        )
        val expectedOutputText =
            "if ()\n" +
            "    abc(\n"

        val indenter = Indenter()
        val actualOutputText = indenter.indent(inputTokens)

        MatcherAssert.assertThat(actualOutputText, equalTo(expectedOutputText))
    }
}
