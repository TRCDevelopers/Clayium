package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.util.getAsItem
import com.github.trcdevelopers.clayium.api.util.toItemStack
import com.github.trcdevelopers.clayium.client.model.MaterialStateMapper
import com.github.trcdevelopers.clayium.common.blocks.properties.CMaterialProperty
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
abstract class BlockMaterialBase(
    blockMaterial: net.minecraft.block.material.Material,
    val mapping: Map<Int, Material>,
) : Block(blockMaterial) {

    abstract fun getMaterialProperty(): CMaterialProperty

    fun getItemStack(material: Material, count: Int = 1): ItemStack {
        return defaultState.withProperty(getMaterialProperty(), material).toItemStack(count)
    }

    fun getCMaterial(meta: Int) = mapping[meta] ?: CMaterials.DUMMY
    fun getCMaterial(stack: ItemStack) = getCMaterial(stack.metadata)
    fun getCMaterial(state: IBlockState) = state.getValue(getMaterialProperty())

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, getMaterialProperty())
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(getMaterialProperty(), getCMaterial(meta))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        val material = state.getValue(getMaterialProperty())
        return mapping.entries.first { it.value == material }.key
    }

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (state in blockState.validStates) {
            items.add(state.toItemStack())
        }
    }

    @SideOnly(Side.CLIENT)
    open fun registerModels() {
        ModelLoader.setCustomStateMapper(this, MaterialStateMapper)
        for (state in blockState.validStates) {
            ModelLoader.setCustomModelResourceLocation(
                this.getAsItem(), this.getMetaFromState(state), MaterialStateMapper.createModelLocation(state)
            )
        }
    }
}