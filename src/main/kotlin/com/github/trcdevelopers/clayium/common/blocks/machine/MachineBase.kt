package com.github.trcdevelopers.clayium.common.blocks.machine

import com.github.trcdevelopers.clayium.common.interfaces.IClayEnergyContainer
import com.github.trcdevelopers.clayium.common.interfaces.IConnectable
import com.github.trcdevelopers.clayium.common.interfaces.IPipable
import com.github.trcdevelopers.clayium.common.interfaces.ITiered
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.util.EnumFacing
import java.util.EnumMap

abstract class MachineBase : Block, ITiered, IClayEnergyContainer, IPipable, IConnectable {
    private val inputMap: MutableMap<EnumFacing, Int> = EnumMap(EnumFacing::class.java)
    private val outputMap: MutableMap<EnumFacing, Int> = EnumMap(EnumFacing::class.java)

    override val tier = 0
    override var clayEnergy = 0L
    override var isPipe = false

    constructor(blockMaterialIn: Material, blockMapColorIn: MapColor) : super(blockMaterialIn, blockMapColorIn)
    constructor(materialIn: Material) : super(materialIn)

    override fun addClayEnergy(energy: Long): Long {
        return energy.let { clayEnergy += it; clayEnergy }
    }

    override fun setInput(facing: EnumFacing, mode: Int) {
        inputMap[facing] = mode
    }

    override fun setOutput(facing: EnumFacing, mode: Int) {
        outputMap[facing] = mode
    }

    override fun clearOutput(facing: EnumFacing) {
        outputMap[facing] = -1
    }

    override fun clearInput(facing: EnumFacing) {
        inputMap[facing] = -1
    }
}
