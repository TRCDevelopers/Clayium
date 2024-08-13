package com.github.trc.clayium.api.laser

import net.minecraft.util.EnumFacing

interface IClayLaser {
    val direction: EnumFacing

    val red: Int
    val green: Int
    val blue: Int

    val energy: Double
    val age: Int

    fun toInt(): Int {
        return (red shl 16) or (green shl 8) or blue
    }
}