package com.github.trcdevelopers.clayium.common.blocks.clay

import com.github.trcdevelopers.clayium.common.Clayium
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
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("")
class BlockEnergizedClay : Block(Material.GROUND) {

    init {
        creativeTab = Clayium.creativeTab
        translationKey = "${Clayium.MOD_ID}.compressed_clay"
        registryName = ResourceLocation(Clayium.MOD_ID, "compressed_clay")
        blockHardness = 0.6f
        soundType = SoundType.GROUND
        lightValue = 0
        setHarvestLevel("shovel", 0)

        this.defaultState = this.blockState.baseState.withProperty(BlockCompressedClay.TIER, 0)
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        super.getSubBlocks(itemIn, items)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(TIER).build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(TIER)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(TIER, meta)
    }

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        return getStateFromMeta(meta)
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    companion object {
        val TIER = PropertyInteger.create("tier", 4, 12)
    }
}