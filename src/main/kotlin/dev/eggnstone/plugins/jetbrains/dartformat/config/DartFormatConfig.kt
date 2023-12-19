package dev.eggnstone.plugins.jetbrains.dartformat.config

class DartFormatConfig(isTest: Boolean = false)
{
    fun toJson(): String
    {
        return "{}"
    }

    val isEnabled
        get(): Boolean
        {
            return removeUnnecessaryCommas
                || removeLineBreaksAfterArrows
                || indentationIsEnabled
        }

    var removeUnnecessaryCommas = isTest

    var removeLineBreaksAfterArrows = isTest

    var indentationIsEnabled = true
    var indentationSpacesPerLevel = 4
}
