package com.github.trc.clayium.common.blocks.material

import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.util.BlockMaterial
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.ClayiumMod
import com.github.trc.clayium.common.blocks.BlockMaterialBase
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.model.ModelLoader

abstract class BlockCompressed(mapping: Map<Int, CMaterial>) : BlockMaterialBase(BlockMaterial.IRON, mapping) {

    init {
        setCreativeTab(ClayiumMod.creativeTab)
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        super.getSubBlocks(itemIn, items)
    }

    override fun registerModels() {
        val loc = ModelResourceLocation(clayiumId("material/compressed_material"), "variant=basic")
        ModelLoader.setCustomStateMapper(this,
            object : StateMapperBase() { override fun getModelResourceLocation(state: IBlockState) = loc }
        )
        for (state in blockState.validStates) {
            ModelLoader.setCustomModelResourceLocation(this.getAsItem(), this.getMetaFromState(state), loc)
        }
    }

    companion object {
        fun create(mapping: Map<Int, CMaterial>): BlockCompressed {
            val materials = mapping.values
            val prop = CMaterialProperty(materials, "material")
            return object : BlockCompressed(mapping) {
                override fun getMaterialProperty() = prop
            }
        }
    }
}