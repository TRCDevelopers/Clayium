package io.github.trcdevelopers.clayium.common.util

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs


class CNumberFormat(
    private val thresholds: DoubleArray,
    private val units: List<String>,
    private val roundingMode: RoundingMode,
    private val decimalFormatStr: String,
) {

    private val decimalFormat = ThreadLocal.withInitial {
        DecimalFormat(decimalFormatStr).also { df -> df.roundingMode = this.roundingMode }
    }

    fun format(number: Double): String {
        val absValue = abs(number)
        var index = thresholds.binarySearch(number)

        if (index < 0) {
            // 見つからなかった場合 (例: 777M)
            // 挿入ポイント (insertion point) を復元
            val insertionPoint = -(index + 1)
            // 使うべき単位は、その挿入ポイントの1つ前
            index = insertionPoint - 1
        }

        // 挿入ポイントが 0 未満（1000より小さい）の場合はフォールバック
        if (index < 0) {
            index = 0
        }

        val divisor = thresholds[index]
        val unit = units[index]
        val displayValue = absValue / divisor

        val displayString = decimalFormat.get().format(displayValue)

        return "$displayString$unit"
    }

    companion object {
        val DEFAULT = CNumberFormat(Thresholds.default, Units.default, RoundingMode.DOWN, "0.000")
        val DEFAULT_NO_DECIMAL = CNumberFormat(Thresholds.default, Units.default, RoundingMode.DOWN, "0.###")
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