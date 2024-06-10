package com.github.trcdevelopers.clayium.common.util

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.IllegalFormatException
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow

//mostly copied from the original version of Clayium
object UtilLocale {
    var SNumerals = arrayOf("", "k", "M", "G", "T", "P", "E", "Z", "Y")
    private const val MAX_TOOLTIP_LINES = 12
    private const val SHIFT_FOR_DETAILS = "__DETAIL__"

    internal fun StackSizeNumeral(stackSize: Long, flag: Boolean): String {
        var k: Int
        var s = ""
        var n = stackSize
        if (n == 0L) {
            return n.toString()
        }
        if (n < 0L) {
            --n
            s = "-"
        }
        if (floor(log10(n.toDouble())).toInt().also { k = it } < 5) {
            return s + n
        }
        val p = min((k / 3).toDouble(), (SNumerals.size - 1).toDouble()).toInt()
        val d: Int = (n.toDouble() / 10.0.pow((k - 2).toDouble())).toInt()
        val flag1 = flag && k % 3 <= 1 && d % 10 == 0
        val flag2 = flag1 && k % 3 == 0 && d / 10 % 10 == 0
        return s + d / 100 + (if (flag2) "" else (if (k % 3 == 0) "." else "") + d / 10 % 10) + (if (flag1) "" else (if (k % 3 == 1) "." else "") + d % 10) + SNumerals[p]
    }

    fun StackSizeNumeral(stackSize: Long): String {
        return StackSizeNumeral(stackSize, false)
    }

    fun CAResonanceNumeral(resonance: Double): String {
        if (resonance < 1.0) {
            return String.format("%.4f", resonance)
        }
        if (resonance < 10.0) {
            return String.format("%.3f", resonance)
        }
        if (resonance < 100.0) {
            return String.format("%.2f", resonance)
        }
        return if (resonance < 1000.0) {
            String.format("%.1f", resonance)
        } else StackSizeNumeral(resonance.toLong())
    }

    fun laserNumeral(laser: Long): String {
        return StackSizeNumeral(laser)
    }

    @SideOnly(value = Side.CLIENT)
    fun laserGui(laser: Long): String {
        return I18n.format("gui.Common.clayLaser", laserNumeral(laser))
    }

    @SideOnly(value = Side.CLIENT)
    fun tierGui(tier: Int): String {
        return I18n.format("gui.Common.tier", tier)
    }

    fun craftTimeNumeral(craftTime: Long): String {
        return StackSizeNumeral(craftTime, true)
    }

    fun rfNumeral(rf: Long): String {
        return String.format("%,d", rf)
    }

    fun localizeUnsafe(str: String): String? {
        val ret = I18n.format(str)
        return if (ret != "" && ret != str) {
            ret
        } else null
    }

    fun localizeAndFormat(str: String, vararg args: Any?): String? {
        var ret = localizeUnsafe(str) ?: return null
        ret = try {
            String.format(ret, *args)
        } catch (e: IllegalFormatException) {
            //ClayiumCore.logger.catching((Throwable)e);
            return str
        }
        return ret
    }

    fun canLocalize(str: String): Boolean {
        return I18n.hasKey(str)
    }

    fun localizeTooltip(str: String): List<String> {
        var flag = true
        var i = 0
        val ret = ArrayList<String>()
        while (flag && i < MAX_TOOLTIP_LINES) {
            val loc = localizeUnsafe(str + ".line" + ++i)
            if (loc != null) {
                if (loc == "__DETAIL__") {
                    if (GuiScreen.isShiftKeyDown()) continue
                    ret.add(localizeUnsafe("tooltip.HoldShiftForDetails") ?: "Failed to load tooltip.HoldShiftForDetails")
                    flag = false
                    continue
                }
                ret.add(loc)
                continue
            }
            flag = false
        }
        return if (ret.isEmpty()) listOf(str) else ret
    }

    /**
     * for translating multiline tooltips.
     * does nothing if the key does not exist.
     *
     * @param tooltip tooltip list that is given in addInformation
     * @param key translation key. line number is appended to this: `$key$i`
     */
    fun formatTooltips(tooltip: MutableList<String>, key: String) {
        var i = 0
        while (i++ <= MAX_TOOLTIP_LINES) {
            if (!I18n.hasKey("$key$i")) break
            val localized = I18n.format("$key$i")
            if (localized == SHIFT_FOR_DETAILS) {
                if (GuiScreen.isShiftKeyDown()) continue
                tooltip.add(I18n.format("tooltip.clayium.HoldShiftForDetails"))
                break // ignore the rest of the lines while not sneaking
            } else {
                tooltip.add(localized)
            }
        }
    }
}
