package io.github.trcdevelopers.clayium.common.util

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs

class CNumberFormat(
    private val thresholds: DoubleArray,
    private val units: List<String>,
    private val roundingMode: RoundingMode,
    /**
     * empty string if no unit.
     * "SCIENTIFICNOTATION" for scientific notation. you can get this String from CNumberFormat.SCI_UNIT_STR
     */
    private val decimalFormatPatternSupplier: (unit: String, displayValue: Double) -> String,
) {

    constructor(thresholds: DoubleArray, units: List<String>, roundingMode: RoundingMode, decimalFormatPattern: String) : this(
        thresholds, units, roundingMode, { _: String, _: Double -> decimalFormatPattern },
    )

    fun format(number: Double): String {
        val absValue = abs(number)
        val sign = if (number < 0) "-" else ""

        if (number == 0.0) {
            val pattern = decimalFormatPatternSupplier("", 0.0)
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

        val decimalFormatPattern = this.decimalFormatPatternSupplier(unit, displayValue)
        val decimalFormat = getDecimalFormat(decimalFormatPattern)

        val displayString = decimalFormat.format(displayValue)
        return "$sign$displayString$unit"
    }

    private fun formatScientificNotation(value: Double): String {
        val pattern = this.decimalFormatPatternSupplier(SCI_UNIT_STR, value)
        val decimalFormat = getDecimalFormat("${pattern}E0")
        return decimalFormat.format(value)
    }

    private fun getDecimalFormat(pattern: String): DecimalFormat {
        return DecimalFormat(pattern).also { df -> df.roundingMode = this.roundingMode }
    }

    companion object {
        val DEFAULT = CNumberFormat(Thresholds.default, Units.default, RoundingMode.DOWN, "0.000")
        val DEFAULT_NO_EXZERO = CNumberFormat(Thresholds.default, Units.default, RoundingMode.DOWN, "0.###")

        const val SCI_UNIT_STR = "SCIENTIFICNOTATION"
    }

    object Thresholds {
        /**
         * micro (1e-6) ... Yotta (1e24)
         */
        val default = doubleArrayOf(1e-6, 1e-3, 1.0, 1e3, 1e6, 1e9, 1e12, 1e15, 1e18, 1e21, 1e24, )
    }

    object Units {
        /**
         * From micro (u) to Yotta (Y)
         */
        val default = listOf("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")
    }

    class NumberUnitPreset(
        val thresholds: DoubleArray,
        val units: List<UnitType>,
    ) {
        companion object {
            val default = NumberUnitPreset(
                doubleArrayOf(1e-6, 1e-3, 1.0, 1e3, 1e6, 1e9, 1e12, 1e15, 1e18, 1e21, 1e24, ),
                listOf("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y").map { UnitType.fromString(it) },
            )
        }
    }

    sealed interface UnitType {
        class Symbol(val symbol: String) : UnitType
        data object ScientificNotation : UnitType
        data object NoUnit : UnitType

        companion object {
            /**
             * Creates a UnitType from a string.
             * empty string for NoUnit, otherwise Symbol.
             */
            fun fromString(str: String): UnitType {
                return when (str) {
                    "" -> NoUnit
                    else -> Symbol(str)
                }
            }
        }
    }
}