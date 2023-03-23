package dev.eggnstone.plugins.jetbrains.dartformat.splitters.iSplitters

import dev.eggnstone.plugins.jetbrains.dartformat.Constants
import dev.eggnstone.plugins.jetbrains.dartformat.DartFormatException
import dev.eggnstone.plugins.jetbrains.dartformat.Tools
import dev.eggnstone.plugins.jetbrains.dartformat.dotlin.DotlinLogger
import dev.eggnstone.plugins.jetbrains.dartformat.dotlin.DotlinTools
import dev.eggnstone.plugins.jetbrains.dartformat.parts.IPart

class MasterSplitter : ISplitter
{
    override val name = "Master"

    fun splitAll(inputText: String): List<IPart>
    {
        val splitResult = split(inputText)
        if (DotlinTools.isNotEmpty(splitResult.remainingText))
        {
            TODO("untested")
            if (Constants.DEBUG) DotlinLogger.log("MasterSplitter.splitAll")
            if (Constants.DEBUG) DotlinLogger.log("  parts:         ${Tools.toDisplayStringForParts(splitResult.parts)}")
            if (Constants.DEBUG) DotlinLogger.log("  remainingText: ${Tools.toDisplayString(splitResult.remainingText)}")
            throw DartFormatException("splitResult.remainingText.isNotEmpty()")
        }

        return splitResult.parts
    }

    override fun split(inputText: String, params: SplitParams): SplitResult
    {
        if (Constants.DEBUG) DotlinLogger.log("MasterSplitter.split: isEnum=${params.isEnum} ${Tools.toDisplayString(Tools.shorten(inputText, 100, true))}")

        val parts = mutableListOf<IPart>()

        var remainingText = inputText
        while (DotlinTools.isNotEmpty(remainingText))
        {
            val splitter = getSplitter(remainingText)
            @Suppress("FoldInitializerAndIfToElvis")
            if (splitter == null)
                return SplitResult(remainingText, parts)

            if (Constants.DEBUG) DotlinLogger.log("Calling '${splitter.name}' splitter ..")
            val splitResult = splitter.split(remainingText, params)
            ///*
            if (Constants.DEBUG) DotlinLogger.log("Result from '${splitter.name}' splitter:")
            if (Constants.DEBUG) DotlinLogger.log("  parts:         ${Tools.toDisplayStringForParts(splitResult.parts)}")
            if (Constants.DEBUG) DotlinLogger.log("  remainingText: ${Tools.toDisplayString(splitResult.remainingText)}")
            //*/
            remainingText = splitResult.remainingText
            //parts += result.parts // dotlin
            parts.addAll(splitResult.parts)
        }

        return SplitResult("", parts)
    }

    fun getSplitter(inputText: String): ISplitter?
    {
        if (DotlinTools.isEmpty(inputText))
            throw DartFormatException("Unexpected empty text.")

        @Suppress("ReplaceGetOrSet") // workaround for dotlin for: for (c in text)
        val c = inputText.get(0).toString() // workaround for dotlin for: for (c in text)

        if (Tools.isWhitespace(c))
            return WhitespaceSplitter()

        if (c != "}")
            return TextSplitter()

        return null
    }
}
