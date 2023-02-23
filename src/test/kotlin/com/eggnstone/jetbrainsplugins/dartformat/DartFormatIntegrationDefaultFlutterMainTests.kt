package com.eggnstone.jetbrainsplugins.dartformat

import TestTools
import com.eggnstone.jetbrainsplugins.dartformat.formatters.Formatter
import com.eggnstone.jetbrainsplugins.dartformat.indenter.Indenter
import com.eggnstone.jetbrainsplugins.dartformat.tokenizers.Tokenizer
import org.junit.Ignore
import org.junit.Test
import java.io.File

class DartFormatIntegrationDefaultFlutterMainTests
{
    private val testDataPath = "src/test/kotlin/com/eggnstone/jetbrainsplugins/dartformat/data/"

    @Test
    fun defaultFlutterMain()
    {
        val inputText = File(testDataPath + "default_flutter_main.input.dart").readText()
        val expectedOutputText = File(testDataPath + "default_flutter_main.expected_output.dart").readText()

        val inputTokens = Tokenizer().tokenize(inputText)
        val actualOutputTokens = Formatter().format(inputTokens)
        val actualOutputText = Indenter().indent(actualOutputTokens)

        TestTools.assertAreEqual(actualOutputText, expectedOutputText)
    }

    @Test
    @Ignore
    fun defaultFlutterMain2()
    {
        val inputText = File(testDataPath + "default_flutter_main2.input.dart").readText()
        val expectedOutputText = File(testDataPath + "default_flutter_main2.expected_output.dart").readText()

        val inputTokens = Tokenizer().tokenize(inputText)
        val actualOutputTokens = Formatter().format(inputTokens)
        val actualOutputText = Indenter().indent(actualOutputTokens)

        TestTools.assertAreEqual(actualOutputText, expectedOutputText)
    }
}