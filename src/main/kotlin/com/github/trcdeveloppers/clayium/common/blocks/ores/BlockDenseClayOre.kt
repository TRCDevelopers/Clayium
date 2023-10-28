package com.github.trcdeveloppers.clayium.common.blocks.ores

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.common.annotation.CBlock
import com.github.trcdeveloppers.clayium.common.items.ItemClayShovel
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import javax.annotation.ParametersAreNonnullByDefault

@CBlock(registryName = "dense_clay_ore")
class BlockDenseClayOre(material: Material?) : Block(material) {
    init {
        creativeTab = Clayium.CreativeTab
        soundType = SoundType.STONE
        setLightLevel(0f)
        setHardness(3f)
        setHarvestLevel("pickaxe", 1)
        setResistance(5f)
    }

    @Suppress("unused")
    constructor() : this(Material.ROCK)

    @ParametersAreNonnullByDefault
    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        return if (player.heldItemMainhand.item is ItemClayShovel) true else super.canHarvestBlock(world, pos, player)
    }
}
