package splitters.iSplitter.textSplitter

import dev.eggnstone.plugins.jetbrains.dartformat.parts.Comment
import dev.eggnstone.plugins.jetbrains.dartformat.parts.IPart
import dev.eggnstone.plugins.jetbrains.dartformat.splitters.iSplitters.TextSplitter
import org.junit.Test
import splitters.SplitterTestTools

class TestMultiLineComments
{
    @Test
    fun onlyComment()
    {
        val inputText =
            "/*\n" +
                "for ()\n" +
                "  abc();\n" +
                "*/"

        val expectedRemainingText = ""
        val expectedPart = Comment(inputText)
        val expectedParts = listOf<IPart>(expectedPart)

        SplitterTestTools.testSplit(TextSplitter(), inputText, expectedRemainingText, expectedParts)
    }
}