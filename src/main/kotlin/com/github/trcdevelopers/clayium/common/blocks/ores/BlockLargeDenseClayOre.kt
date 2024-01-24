package com.github.trcdevelopers.clayium.common.blocks.ores

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.annotation.CBlock
import com.github.trcdevelopers.clayium.common.items.ItemClayShovel
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import javax.annotation.ParametersAreNonnullByDefault

@CBlock(registryName = "large_dense_clay_ore")
class BlockLargeDenseClayOre(material: Material) : Block(material) {
    init {
        creativeTab = Clayium.CreativeTab
        soundType = SoundType.STONE
        setLightLevel(0f)
        setHarvestLevel("pickaxe", 1)
        setResistance(5f)
        setHardness(3f)
    }

    @Suppress("unused")
    constructor() : this(Material.ROCK)

    @ParametersAreNonnullByDefault
    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        return if (player.heldItemMainhand.item is ItemClayShovel) true else super.canHarvestBlock(world, pos, player)
    }
}
