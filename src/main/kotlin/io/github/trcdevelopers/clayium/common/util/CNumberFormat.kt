package io.github.trcdevelopers.clayium.common.util

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs

class CNumberFormat(
    numberUnitPreset: NumberUnitPreset,
    private val roundingMode: RoundingMode,
    private val decimalFormatPatternSupplier: (unit: DisplayUnit, displayValue: Double) -> String,
) {

    private val thresholds = numberUnitPreset.thresholds
    private val units = numberUnitPreset.units

    constructor(numberUnitPreset: NumberUnitPreset, roundingMode: RoundingMode, decimalFormatPattern: String) : this(
        numberUnitPreset, roundingMode, { _: DisplayUnit, _: Double -> decimalFormatPattern },
    )

    fun format(number: Double): String {
        val absValue = abs(number)
        val sign = if (number < 0) "-" else ""

        if (number == 0.0) {
            val pattern = decimalFormatPatternSupplier(DisplayUnit.NoUnit, 0.0)
            return getDecimalFormat(pattern).format(number)
        }

        if (absValue < thresholds.first() || absValue > thresholds.last()) {
            return this.formatScientificNotation(number)
        }

        var index = thresholds.binarySearch(absValue)

        if (index < 0) {
            // Not found.
            // binarySearch returns (-insertionPoint - 1), so let's recover that here.
            val insertionPoint = -(index + 1)
            // Unit before the insertion point should be used
            index = insertionPoint - 1
        }

        val divisor = thresholds[index]
        val unit = units[index]
        val displayValue = absValue / divisor

        val decimalFormatPattern = this.decimalFormatPatternSupplier(DisplayUnit.fromString(unit), displayValue)
        val decimalFormat = getDecimalFormat(decimalFormatPattern)

        val displayString = decimalFormat.format(displayValue)
        return "$sign$displayString$unit"
    }

    private fun formatScientificNotation(value: Double): String {
        val pattern = this.decimalFormatPatternSupplier(DisplayUnit.ScientificNotation, value)
        val decimalFormat = getDecimalFormat("${pattern}E0")
        return decimalFormat.format(value)
    }

    private fun getDecimalFormat(pattern: String): DecimalFormat {
        return DecimalFormat(pattern).also { df -> df.roundingMode = this.roundingMode }
    }

    companion object {
        val DEFAULT = CNumberFormat(NumberUnitPreset.default, RoundingMode.DOWN, "0.000")
        val DEFAULT_NO_EXZERO = CNumberFormat(NumberUnitPreset.default, RoundingMode.DOWN, "0.###")
    }

    class NumberUnitPreset(
        val thresholds: DoubleArray,
        /**
         * You can use Empty String for no unit.
         */
        val units: List<String>,
    ) {
        init {
            require(thresholds.isNotEmpty()) { "Thresholds must not be empty." }
            require(units.isNotEmpty()) { "Units must not be empty." }
            require(thresholds.size == units.size) { "Thresholds and units must have the same size." }
            for (i in 1..<thresholds.size) {
                require(thresholds[i] > thresholds[i - 1]) { "Thresholds must be strictly increasing." }
            }
        }

        companion object {
            val default = NumberUnitPreset(
                doubleArrayOf(1e-6, 1e-3, 1.0, 1e3, 1e6, 1e9, 1e12, 1e15, 1e18, 1e21, 1e24, ),
                listOf("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y"),
            )
        }
    }

    sealed interface DisplayUnit {
        class Symbol(val symbol: String) : DisplayUnit
        data object ScientificNotation : DisplayUnit
        data object NoUnit : DisplayUnit

        companion object {
            fun fromString(unit: String): DisplayUnit {
                return when (unit) {
                    "" -> NoUnit
                    else -> Symbol(unit)
                }
            }
        }
    }
}