package com.github.trcdeveloppers.clayium.common.util;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

//everything from Original Clayium
public class UtilLocale {
    static String[] CENumerals = new String[]{"u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y"};
    static String[] SNumerals = new String[]{"", "k", "M", "G", "T", "P", "E", "Z", "Y"};
    private static final int maxLineTooltip = 12;

    public static String ClayEnergyNumeral(double ce, boolean flag) {
        double n = ce * 10.0;
        String s = "";
        if (n == 0.0) {
            return String.valueOf(n);
        }
        if (n < 0.0) {
            n -= 1.0;
            s = "-";
        }
        int k = (int) Math.floor(Math.log10(n));
        int p = Math.min(k / 3, CENumerals.length - 1);
        int d = (int) (n * 1000.0 / Math.pow(10.0, p * 3));
        return s + UtilLocale.ClayEnergyNumeral(d, p, p == 0 || flag);
    }

    public static String ClayEnergyNumeral(double ce) {
        return UtilLocale.ClayEnergyNumeral(ce, true);
    }

    public static String ClayEnergyNumeral(long ce, boolean flag) {
        long n = ce * 10L;
        String s = "";
        if (n == 0L) {
            return String.valueOf(n);
        }
        if (n < 0L) {
            --n;
            s = "-";
        }
        int k = (int) Math.floor(Math.log10(n));
        int p = Math.min(k / 3, CENumerals.length - 1);
        int d = (int) ((double) n * 1000.0 / Math.pow(10.0, p * 3));
        return s + UtilLocale.ClayEnergyNumeral(d, p, p == 0 || flag);
    }

    public static String ClayEnergyNumeral(long ce) {
        return UtilLocale.ClayEnergyNumeral(ce, true);
    }

    protected static String ClayEnergyNumeral(int d, int p, boolean flag) {
        if (d % 10 != 0 || !flag) {
            return d / 1000 + "." + d / 100 % 10 + d / 10 % 10 + d % 10 + CENumerals[p];
        }
        if (d % 100 != 0) {
            return d / 1000 + "." + d / 100 % 10 + d / 10 % 10 + CENumerals[p];
        }
        if (d % 1000 != 0) {
            return d / 1000 + "." + d / 100 % 10 + CENumerals[p];
        }
        return d / 1000 + CENumerals[p];
    }

    protected static String StackSizeNumeral(long stackSize, boolean flag) {
        int k;
        String s = "";
        long n = stackSize;
        if (n == 0L) {
            return String.valueOf(n);
        }
        if (n < 0L) {
            --n;
            s = "-";
        }
        if ((k = (int) Math.floor(Math.log10(n))) < 5) {
            return s + n;
        }
        int p = Math.min(k / 3, SNumerals.length - 1);
        int d = (int) ((double) n / Math.pow(10.0, k - 2));
        boolean flag1 = flag && k % 3 <= 1 && d % 10 == 0;
        boolean flag2 = flag1 && k % 3 == 0 && d / 10 % 10 == 0;
        return s + d / 100 + (flag2 ? "" : (k % 3 == 0 ? "." : "") + d / 10 % 10) + (flag1 ? "" : (k % 3 == 1 ? "." : "") + d % 10) + SNumerals[p];
    }

    public static String StackSizeNumeral(long stackSize) {
        return UtilLocale.StackSizeNumeral(stackSize, false);
    }

    public static String CAResonanceNumeral(double resonance) {
        if (resonance < 1.0) {
            return String.format("%.4f", resonance);
        }
        if (resonance < 10.0) {
            return String.format("%.3f", resonance);
        }
        if (resonance < 100.0) {
            return String.format("%.2f", resonance);
        }
        if (resonance < 1000.0) {
            return String.format("%.1f", resonance);
        }
        return UtilLocale.StackSizeNumeral((long) resonance);
    }

    public static String laserNumeral(long laser) {
        return UtilLocale.StackSizeNumeral(laser);
    }

    @SideOnly(value = Side.CLIENT)
    public static String laserGui(long laser) {
        return I18n.format("gui.Common.clayLaser", UtilLocale.laserNumeral(laser));
    }

    @SideOnly(value = Side.CLIENT)
    public static String tierGui(int tier) {
        return I18n.format("gui.Common.tier", tier);
    }

    public static String craftTimeNumeral(long craftTime) {
        return UtilLocale.StackSizeNumeral(craftTime, true);
    }

    public static String rfNumeral(long rf) {
        return String.format("%,d", rf);
    }

    public static String localizeUnsafe(String str) {
        String ret = I18n.format(str);
        if (!ret.equals("") && !ret.equals(str)) {
            return ret;
        }
        return null;
    }

    public static String localizeAndFormat(String str, Object... args) {
        String ret = UtilLocale.localizeUnsafe(str);
        if (ret == null) {
            return null;
        }
        try {
            ret = String.format(ret, args);
        } catch (IllegalFormatException e) {
            //ClayiumCore.logger.catching((Throwable)e);
            return str;
        }
        return ret;
    }

    public static boolean canLocalize(String str) {
        return I18n.hasKey(str);
    }

    public static List<String> localizeTooltip(String str) {
        boolean flag = true;
        int i = 0;
        ArrayList<String> ret = new ArrayList<>();
        while (flag && i < maxLineTooltip) {
            String loc = UtilLocale.localizeUnsafe(str + ".line" + ++i);
            if (loc != null) {
                if (loc.equals("__DETAIL__")) {
                    if (GuiScreen.isShiftKeyDown()) continue;
                    ret.add(UtilLocale.localizeUnsafe("tooltip.HoldShiftForDetails"));
                    flag = false;
                    continue;
                }
                ret.add(loc);
                continue;
            }
            flag = false;
        }
        return ret.isEmpty() ? null : ret;
    }
}
