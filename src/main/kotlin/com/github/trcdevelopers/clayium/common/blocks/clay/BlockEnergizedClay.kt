package com.github.trcdevelopers.clayium.common.blocks.clay

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
class BlockEnergizedClay : Block(Material.GROUND) {

    init {
        setSoundType(SoundType.GROUND)
        setLightLevel(0f)
        setHardness(0.6f)
        setHarvestLevel("shovel", 0)

        this.defaultState = this.blockState.baseState.withProperty(META, 0)
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (i in 0..8) {
            items.add(ItemStack(this, 1, i))
        }
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(META).build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(META)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(META, meta)
    }

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        return getStateFromMeta(meta)
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    companion object {
        private val META = PropertyInteger.create("meta", 0, 8)
    }
}