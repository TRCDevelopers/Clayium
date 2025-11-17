package io.github.trcdevelopers.clayium.common.util

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs

class CNumberFormat(
    private val presets: Presets,
) {

    fun format(number: Double): String {
        val absValue = abs(number)
        val sign = if (number < 0) "-" else ""

        if (number == 0.0) {
            val pattern = presets.decimalFormatPatternSupplier(DisplayUnit.NoUnit, 0.0)
            return getDecimalFormat(pattern).format(number)
        }

        if (absValue < presets.thresholds.first() || absValue > presets.thresholds.last()) {
            return this.formatScientificNotation(number)
        }

        var index = presets.thresholds.binarySearch(absValue)

        if (index < 0) {
            // Not found.
            // binarySearch returns (-insertionPoint - 1), so let's recover that here.
            val insertionPoint = -(index + 1)
            // Unit before the insertion point should be used
            index = insertionPoint - 1
        }

        val divisor = presets.thresholds[index]
        val unit = presets.units[index]
        val displayValue = absValue / divisor

        val decimalFormatPattern = presets.decimalFormatPatternSupplier(DisplayUnit.fromString(unit), displayValue)
        val decimalFormat = getDecimalFormat(decimalFormatPattern)

        val displayString = decimalFormat.format(displayValue)
        return "$sign$displayString$unit"
    }

    private fun formatScientificNotation(value: Double): String {
        val pattern = presets.decimalFormatPatternSupplier(DisplayUnit.ScientificNotation, value)
        val decimalFormat = getDecimalFormat("${pattern}E0")
        return decimalFormat.format(value)
    }

    private fun getDecimalFormat(pattern: String): DecimalFormat {
        return DecimalFormat(pattern).also { df -> df.roundingMode = presets.roundingMode }
    }

    fun copyToBuilder(): Builder {
        return Builder()
            .thresholds(*presets.thresholds)
            .units(presets.units)
            .roundingMode(presets.roundingMode)
            .decimalFormatPatternProvider(presets.decimalFormatPatternSupplier)
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

    class Presets(
        val thresholds: DoubleArray,
        /**
         * You can use Empty String for no unit.
         */
        val units: List<String>,
        val roundingMode: RoundingMode,
        val maxLength: Int,
        val decimalFormatPatternSupplier: (unit: DisplayUnit, displayValue: Double) -> String,
    ) {
        init {
            require(thresholds.isNotEmpty()) { "Thresholds must not be empty." }
            require(units.isNotEmpty()) { "Units must not be empty." }
            require(thresholds.size == units.size) { "Thresholds and units must have the same size." }
            for (i in 1..<thresholds.size) {
                require(thresholds[i] > thresholds[i - 1]) { "Thresholds must be strictly increasing." }
            }
        }
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
            val preset = Presets(thresholds, units, roundingMode, maxLength, decimalFormatPatternSupplier)
            return CNumberFormat(preset)
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