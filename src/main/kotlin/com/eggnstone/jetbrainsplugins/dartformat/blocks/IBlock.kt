package com.eggnstone.jetbrainsplugins.dartformat.blocks

interface IBlock
{
    //fun recreate(): String

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
}
