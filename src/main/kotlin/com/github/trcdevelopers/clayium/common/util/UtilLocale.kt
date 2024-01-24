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

//everything from Original Clayium
object UtilLocale {
    var CENumerals = arrayOf("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")
    var SNumerals = arrayOf("", "k", "M", "G", "T", "P", "E", "Z", "Y")
    private const val maxLineTooltip = 12
    fun ClayEnergyNumeral(ce: Double, flag: Boolean): String {
        var n = ce * 10.0
        var s = ""
        if (n == 0.0) {
            return n.toString()
        }
        if (n < 0.0) {
            n -= 1.0
            s = "-"
        }
        val k = floor(log10(n)).toInt()
        val p = min((k / 3).toDouble(), (CENumerals.size - 1).toDouble()).toInt()
        val d: Int = (n * 1000.0 / 10.0.pow((p * 3).toDouble())).toInt()
        return s + ClayEnergyNumeral(d, p, p == 0 || flag)
    }

    fun ClayEnergyNumeral(ce: Double): String {
        return ClayEnergyNumeral(ce, true)
    }

    fun ClayEnergyNumeral(ce: Long, flag: Boolean): String {
        var n = ce * 10L
        var s = ""
        if (n == 0L) {
            return n.toString()
        }
        if (n < 0L) {
            --n
            s = "-"
        }
        val k = floor(log10(n.toDouble())).toInt()
        val p = min((k / 3).toDouble(), (CENumerals.size - 1).toDouble()).toInt()
        val d: Int = (n.toDouble() * 1000.0 / 10.0.pow((p * 3).toDouble())).toInt()
        return s + ClayEnergyNumeral(d, p, p == 0 || flag)
    }

    fun ClayEnergyNumeral(ce: Long): String {
        return ClayEnergyNumeral(ce, true)
    }

    internal fun ClayEnergyNumeral(d: Int, p: Int, flag: Boolean): String {
        if (d % 10 != 0 || !flag) {
            return (d / 1000).toString() + "." + d / 100 % 10 + d / 10 % 10 + d % 10 + CENumerals[p]
        }
        if (d % 100 != 0) {
            return (d / 1000).toString() + "." + d / 100 % 10 + d / 10 % 10 + CENumerals[p]
        }
        return if (d % 1000 != 0) {
            (d / 1000).toString() + "." + d / 100 % 10 + CENumerals[p]
        } else (d / 1000).toString() + CENumerals[p]
    }

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
        while (flag && i < maxLineTooltip) {
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
}
