package com.eggnstone.jetbrainsplugins.dartformat.tokenizer

import com.eggnstone.jetbrainsplugins.dartformat.tokens.EndOfLineCommentToken
import com.eggnstone.jetbrainsplugins.dartformat.tokens.UnknownToken
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class TokenizeEndOfLineComments
{
    @Test
    fun multiLineCommentAtTextEnd()
    {
        val inputText = "abc//comment"
        val expectedTokens = arrayListOf(
            UnknownToken("abc"),
            EndOfLineCommentToken("comment")
        )

        val tokenizer = Tokenizer()
        val commentTokenizer = CommentTokenizer()
        val actualTokens = commentTokenizer.tokenize(inputText)
        val actualText = tokenizer.recreate(actualTokens)

        assertThat(actualTokens, equalTo(expectedTokens))
        assertThat(actualText, equalTo(inputText))
    }
}