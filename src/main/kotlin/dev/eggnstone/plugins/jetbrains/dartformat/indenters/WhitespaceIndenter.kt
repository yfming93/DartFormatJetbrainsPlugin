package dev.eggnstone.plugins.jetbrains.dartformat.indenters

import dev.eggnstone.plugins.jetbrains.dartformat.parts.IPart

class WhitespaceIndenter : IIndenter
{
    override fun indentPart(part: IPart): String
    {
        return ""
    }
}