package com.eggnstone.jetbrainsplugins.dartformat.config

class DartFormatConfig(isTest: Boolean = false)
{
    var removeUnnecessaryCommas = isTest

    var removeLineBreaksAfterArrows = isTest

    var indentationIsEnabled = isTest
    var indentationSpacesPerLevel = 4
}
