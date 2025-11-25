package io.github.trcdevelopers.clayium.common.util

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.log10

class CNumberFormat(
    private val thresholds: DoubleArray,
    /**
     * You can use Empty String for no unit.
     */
    private val units: List<String>,
    private val roundingMode: RoundingMode,
    private val maxLength: Int,
    private val decimalFormatPatternSupplier: (unit: DisplayUnit, displayValue: Double) -> String,
) {
    init {
        require(thresholds.isNotEmpty()) { "Thresholds must not be empty." }
        require(units.isNotEmpty()) { "Units must not be empty." }
        require(thresholds.size == units.size) { "Thresholds and units must have the same size." }
        for (i in 1..<thresholds.size) {
            require(thresholds[i] > thresholds[i - 1]) { "Thresholds must be strictly increasing." }
        }
    }

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

        var maxLength = this.maxLength - unit.length
        val intDigits = if (displayValue >= 1) {
            (log10(displayValue) + 1).toInt()
        } else {
            1 // "0"
        }
        maxLength -= intDigits

        val decimalFormatPattern = decimalFormatPatternSupplier(DisplayUnit.fromString(unit), displayValue)
        val decimalFormat = getDecimalFormat(decimalFormatPattern)

        val displayString = if (this.maxLength != MAX_LEN_UNLIMITED) {
            val m1 = decimalFormat.maximumFractionDigits
            val m2 = decimalFormat.minimumFractionDigits
            decimalFormat.maximumFractionDigits = maxLength
            val s = decimalFormat.format(displayValue)
            decimalFormat.maximumFractionDigits = m1
            decimalFormat.minimumFractionDigits = m2
            s
        } else {
            decimalFormat.format(displayValue)
        }

        return "$sign$displayString$unit"
    }

    private fun formatScientificNotation(value: Double): String {
        val pattern = decimalFormatPatternSupplier(DisplayUnit.ScientificNotation, value)
        val decimalFormat = getDecimalFormat("${pattern}E0")
        return decimalFormat.format(value)
    }

    private fun getDecimalFormat(pattern: String): DecimalFormat {
        return DecimalFormat(pattern).also { df -> df.roundingMode = roundingMode }
    }

    fun copyToBuilder(): Builder {
        return Builder()
            .thresholds(*thresholds)
            .units(units)
            .roundingMode(roundingMode)
            .maxLength(maxLength)
            .decimalFormatPatternProvider(decimalFormatPatternSupplier)
    }

    companion object {
        const val MAX_LEN_UNLIMITED = -1

        /**
         * RoundingMode: DOWN
         * DecimalFormat: "0.000"
         * MaxLength: Unlimited
         */
        val DEFAULT = Builder()
            .thresholds(1e-6, 1e-3, 1.0, 1e3, 1e6, 1e9, 1e12, 1e15, 1e18, 1e21, 1e24)
            .units("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")
            .roundingMode(RoundingMode.DOWN)
            .decimalFormat("0.000")
            .build()
        val DEFAULT_NO_EXZERO = DEFAULT.copyToBuilder()
            .decimalFormat("0")
            .build()
    }

    class Builder {
        private var thresholds: DoubleArray = doubleArrayOf()
        private var units: List<String> = listOf()
        private var roundingMode: RoundingMode = RoundingMode.HALF_UP
        private var maxLength: Int = MAX_LEN_UNLIMITED
        private var decimalFormatPatternSupplier: (unit: DisplayUnit, displayValue: Double) -> String =
            { _, _ -> "0.###" }

        fun thresholds(vararg thresholds: Double) = apply { this.thresholds = thresholds }

        fun units(units: List<String>) = apply { this.units = units }
        fun units(vararg units: String) = apply { this.units = units.toList() }

        fun roundingMode(roundingMode: RoundingMode) = apply {
            this.roundingMode = roundingMode
        }

        fun maxLength(maxLength: Int) = apply { this.maxLength = maxLength }

        fun decimalFormatPatternProvider(
            supplier: (unit: DisplayUnit, displayValue: Double) -> String,
        ) = apply {
            this.decimalFormatPatternSupplier = supplier
        }

        fun decimalFormat(pattern: String) = apply {
            this.decimalFormatPatternSupplier = { _, _ -> pattern }
        }

        fun build(): CNumberFormat {
            return CNumberFormat(thresholds, units, roundingMode, maxLength, decimalFormatPatternSupplier)
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