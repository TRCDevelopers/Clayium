package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.api.util.toItemStack
import com.github.trc.clayium.client.model.MaterialStateMapper
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
abstract class BlockMaterialBase(
    blockMaterial: net.minecraft.block.material.Material,
    val mapping: Map<Int, CMaterial>,
) : Block(blockMaterial) {

    abstract fun getMaterialProperty(): CMaterialProperty

    fun getItemStack(material: CMaterial, count: Int = 1): ItemStack {
        return defaultState.withProperty(getMaterialProperty(), material).toItemStack(count)
    }

    fun getCMaterial(meta: Int) = mapping[meta] ?: mapping.values.first()

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
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        val material = getCMaterial(stack.metadata)
        if (material.tier != null) {
            tooltip.add(I18n.format("tooltip.clayium.tier", material.tier.numeric))
        }
    }

    @SideOnly(Side.CLIENT)
    open fun registerModels() {
        ModelLoader.setCustomStateMapper(this, MaterialStateMapper)
        for (state in blockState.validStates) {
            ModelLoader.setCustomModelResourceLocation(
                this.getAsItem(),
                this.getMetaFromState(state),
                MaterialStateMapper.createModelLocation(state)
            )
        }
    }
}
