package dev.eggnstone.plugins.jetbrains.dartformat.indenters

import dev.eggnstone.plugins.jetbrains.dartformat.dotlin.DotlinLogger
import dev.eggnstone.plugins.jetbrains.dartformat.dotlin.StringWrapper
import dev.eggnstone.plugins.jetbrains.dartformat.indenters.iIndenters.MasterIndenter
import dev.eggnstone.plugins.jetbrains.dartformat.parts.IPart
import dev.eggnstone.plugins.jetbrains.dartformat.splitters.LineSplitter
import dev.eggnstone.plugins.jetbrains.dartformat.tools.Tools

class BlockIndenter(private val spacesPerLevel: Int)
{
    companion object
    {
        private val lineSplitter = LineSplitter()
    }

    private var masterIndenter = MasterIndenter(spacesPerLevel)

    fun indentParts(parts: List<IPart>): String
    {
        if (DotlinLogger.isEnabled) DotlinLogger.log("BlockIndenter.indentParts(${Tools.toDisplayStringForParts(parts)})")

        val body = masterIndenter.indentParts(parts)
        if (DotlinLogger.isEnabled) DotlinLogger.log("BlockIndenter.indentParts: body: ${Tools.toDisplayStringShort(body)}")
        val lines = lineSplitter.split(body, trim = false)

        //if (DotlinLogger.isEnabled) DotlinLogger.log("  Lines :${lines.size}")
        val indentedBody = StringBuilder()
        @Suppress("ReplaceManualRangeWithIndicesCalls") // workaround for dotlin
        for (i in 0 until lines.size) // workaround for dotlin
        {
            @Suppress("ReplaceGetOrSet") // workaround for dotlin
            val line = lines.get(i) // workaround for dotlin
            if (DotlinLogger.isEnabled) DotlinLogger.log("  Line #$i: ${Tools.toDisplayStringShort(line)}")

            if (i == 0 && !Tools.containsLineBreak(line))
            {
                indentedBody.append(line)
                continue
            }

            val pad = if (StringWrapper.isBlank(line)) "" else StringWrapper.getSpaces(spacesPerLevel)
            indentedBody.append(pad + line)
        }

        //if (DotlinLogger.isEnabled) DotlinLogger.log("BlockIndenter.indentParts: indentedBody: ${Tools.toDisplayStringShort(indentedBody)}")
        return indentedBody.toString()
    }
}
