package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineTemp.Companion.IS_PIPE
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool

/**
 * Root of all Clayium BlockMachines.
 *
 * - can be piped (see [IS_PIPE])
 */
class BlockMachineTemp(

): Block(Material.IRON) {
    init {
        setCreativeTab(Clayium.creativeTab)
        setHardness(5.0f)
        setHarvestLevel("pickaxe", 1)
        setSoundType(SoundType.METAL)
    }
    companion object {
        val IS_PIPE = PropertyBool.create("is_pipe")
    }
}