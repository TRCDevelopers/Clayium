package io.github.trcdevelopers.clayium.common.util

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs

class CNumberFormat(
    private val thresholds: DoubleArray,
    private val units: List<String>,
    private val roundingMode: RoundingMode,
    private val decimalFormatStr: String,
    /**
     * If true, DecimalFormat string is dynamically determined by suffix & displayValue to fix the length of Formatted String as possible.
     * e.g. 0.000, 1.234, 12.3k, 56.7M, 888G (4 chars).
     *
     * Note: decimalFormatStr is still used in scientific notation (number too big/small).
     */
    private val lengthFixed: Boolean = false,
) {

    private val decimalFormat = ThreadLocal.withInitial {
        DecimalFormat(decimalFormatStr).also { df -> df.roundingMode = this.roundingMode }
    }

    fun format(number: Double): String {
        val absValue = abs(number)

        if (absValue < thresholds.first() || absValue > thresholds.last()) {
            return this.formatScientificNotation(number)
        }

        var index = thresholds.binarySearch(number)

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

        return if (this.lengthFixed) {
            val pattern = this.getDecimalFormatForLengthFixed(unit, displayValue)
            getDecimalFormat(pattern).format(displayValue)
        } else {
            val displayString = decimalFormat.get().format(displayValue)
            "$displayString$unit"
        }
    }

    /**
     * No unit = empty string.
     */
    private fun getDecimalFormatForLengthFixed(unit: String, displayValue: Double): String {
        return if (unit.isEmpty()) {
            when {
                displayValue < 10.0 -> "0.000" // 1.234
                displayValue < 100.0 -> "0.00" // 12.34
                else -> "0.0" // 123.4
            }
        } else {
            when {
                displayValue < 10.0 -> "0.00" // 1.23k
                displayValue < 100.0 -> "0.0" //12.3k
                else -> "0" // 123k
            }
        }
    }

    private fun formatScientificNotation(value: Double): String {
        val decimalFormat = getDecimalFormat("${decimalFormatStr}E0")
        return decimalFormat.format(value)
    }

    private fun getDecimalFormat(pattern: String): DecimalFormat {
        return DecimalFormat(pattern).also { df -> df.roundingMode = this.roundingMode }
    }

    companion object {
        val DEFAULT = CNumberFormat(Thresholds.default, Units.default, RoundingMode.DOWN, "0.000", false)
        val DEFAULT_NO_DECIMAL = CNumberFormat(Thresholds.default, Units.default, RoundingMode.DOWN, "0.###", false)
    }

    object Thresholds {
        /**
         * micro (1e-6) ... Yotta (1e24)
         */
        val default = doubleArrayOf(
            1e-6, 1e-3,
            1.0,
            1e3, 1e6, 1e9, 1e12, 1e15, 1e18, 1e21, 1e24,
        )
    }

    object Units {
        /**
         * From micro (u) to Yotta (Y)
         */
        val default = listOf("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")
    }
}