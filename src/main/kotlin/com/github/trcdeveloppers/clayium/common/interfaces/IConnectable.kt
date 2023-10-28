package com.github.trcdeveloppers.clayium.common.interfaces

import net.minecraft.util.EnumFacing

interface IConnectable {
    fun getInput(facing: EnumFacing): Int {
        return -1
    }

    fun getOutput(facing: EnumFacing): Int {
        return -1
    }

    fun rollInput(facing: EnumFacing): Int {
        return -1
    }

    fun rollOutput(facing: EnumFacing): Int {
        return -1
    }

    fun setInput(facing: EnumFacing, mode: Int)
    fun setOutput(facing: EnumFacing, mode: Int)
    fun clearInput(facing: EnumFacing)
    fun clearOutput(facing: EnumFacing)
}
