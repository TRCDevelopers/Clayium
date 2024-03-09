package com.github.trcdevelopers.clayium.common.blocks.ores

import com.github.trcdevelopers.clayium.common.items.ItemClayShovel
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockDenseClayOre : Block(Material.ROCK) {
    init {
        setSoundType(SoundType.STONE)
        setLightLevel(0f)
        setResistance(5f)
        setHardness(3f)
        setHarvestLevel("pickaxe", 1)
    }

    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        return if (player.heldItemMainhand.item is ItemClayShovel) true else super.canHarvestBlock(world, pos, player)
    }
}
