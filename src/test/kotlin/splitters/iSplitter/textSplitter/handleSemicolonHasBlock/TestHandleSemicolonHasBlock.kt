package splitters.iSplitter.textSplitter.handleSemicolonHasBlock

import TestTools
import dev.eggnstone.plugins.jetbrains.dartformat.parts.MultiBlock
import dev.eggnstone.plugins.jetbrains.dartformat.splitters.iSplitters.TextSplitter
import dev.eggnstone.plugins.jetbrains.dartformat.splitters.iSplitters.TextSplitterHandleSplitResult
import dev.eggnstone.plugins.jetbrains.dartformat.splitters.iSplitters.TextSplitterState
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class TestHandleSemicolonHasBlock
{
    @Test
    fun TODO()
    {
        val header = "HEADER"

        val inputState = TextSplitterState("")
        inputState.currentText = "abc"
        inputState.remainingText = ";}"
        inputState.headers.add(header)
        inputState.partLists.add(listOf())

        val expectedFooter = "abc;"
        val expectedRemainingText = "}"
        val expectedState = TextSplitterState("")
        expectedState.currentText = ""
        expectedState.remainingText = expectedRemainingText
        expectedState.headers.add(header)
        expectedState.footer = expectedFooter

        val expectedParts = listOf(MultiBlock.single(header, expectedFooter))

        val actualHandleResult = TextSplitter.handleSemicolonHasBlock(inputState) as TextSplitterHandleSplitResult

        val splitResult = actualHandleResult.splitResult
        TestTools.assertAreEqual("splitResult.remainingText", splitResult.remainingText, expectedRemainingText)
        MatcherAssert.assertThat("splitResult.parts", splitResult.parts, CoreMatchers.equalTo(expectedParts))
    }
}
