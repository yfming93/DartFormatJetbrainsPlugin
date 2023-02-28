package indenters.master.indentParts

import dev.eggnstone.plugins.jetbrains.dartformat.indenters.MasterIndenter
import dev.eggnstone.plugins.jetbrains.dartformat.parts.Statement
import dev.eggnstone.plugins.jetbrains.dartformat.parts.Whitespace
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class TestSimple
{
    @Test
    fun whitespaceBeforeStatement()
    {
        val inputParts = listOf(Whitespace("\n\r\t "), Statement("abc();"))

        val expectedText = "abc();"

        val actualText = MasterIndenter().indentParts(inputParts)

        MatcherAssert.assertThat(actualText, CoreMatchers.equalTo(expectedText))
    }

    @Test
    fun whitespaceAfterStatement()
    {
        val inputParts = listOf(Statement("abc();"), Whitespace("\n\r\t "))

        val expectedText = "abc();"

        val actualText = MasterIndenter().indentParts(inputParts)

        MatcherAssert.assertThat(actualText, CoreMatchers.equalTo(expectedText))
    }

    @Test
    fun whitespaceBeforeAndAfterStatement()
    {
        val inputParts = listOf(Whitespace("\n\r\t "), Statement("abc();"), Whitespace("\n\r\t "))

        val expectedText = "abc();"

        val actualText = MasterIndenter().indentParts(inputParts)

        MatcherAssert.assertThat(actualText, CoreMatchers.equalTo(expectedText))
    }

    @Test
    fun statementsWithoutWhitespace()
    {
        val inputParts = listOf(Statement("abc();"), Statement("def();"))

        val expectedText = "abc();def();"

        val actualText = MasterIndenter().indentParts(inputParts)

        MatcherAssert.assertThat(actualText, CoreMatchers.equalTo(expectedText))
    }

    @Test
    fun whitespaceBetweenStatements()
    {
        val inputParts = listOf(Statement("abc();"), Whitespace("\n\r\t "), Statement("def();"))

        val expectedText = "abc(); def();"

        val actualText = MasterIndenter().indentParts(inputParts)

        MatcherAssert.assertThat(actualText, CoreMatchers.equalTo(expectedText))
    }
}