package io.github.trcdevelopers.clayium.integration.modularui

import com.cleanroommc.modularui.utils.NumberFormat
import java.math.RoundingMode

object CNumFormat {

    val NUMBER_FORMAT: NumberFormat.Params = NumberFormat.DEFAULT.copyToBuilder()
        .roundingMode(RoundingMode.DOWN)
        .build()

    fun format(number: Double): String {
        return NumberFormat.format(number, NUMBER_FORMAT)
    }
}