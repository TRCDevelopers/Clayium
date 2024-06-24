package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.util.toItemStack
import com.github.trcdevelopers.clayium.common.blocks.properties.CMaterialProperty
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

@Suppress("OVERRIDE_DEPRECATION")
abstract class BlockMaterialBase(material: net.minecraft.block.material.Material) : Block(material) {
    abstract fun getMaterialProperty(): CMaterialProperty

    fun getItemStack(material: Material, count: Int = 1): ItemStack {
        return defaultState.withProperty(getMaterialProperty(), material).toItemStack(count)
    }

    fun getCMaterial(meta: Int): Material {
        if (meta < 0 || meta >= getMaterialProperty().allowedValues.size) {
            return getMaterialProperty().allowedValues[0]
        }
        return getMaterialProperty().allowedValues[meta]
    }

    fun getCMaterial(stack: ItemStack) = getCMaterial(stack.metadata)
    fun getCMaterial(state: IBlockState) = state.getValue(getMaterialProperty())

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, getMaterialProperty())
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(getMaterialProperty(), getCMaterial(meta))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return getMaterialProperty().allowedValues.indexOf(state.getValue(getMaterialProperty()))
    }

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (state in blockState.validStates) {
            items.add(state.toItemStack())
        }
    }
}