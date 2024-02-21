package com.github.trcdevelopers.clayium.common.blocks.clay

import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemBlockCompressedClay(block: BlockCompressedClay) : ItemBlock(block) {
    init {
        hasSubtypes = true
        creativeTab = Clayium.creativeTab
        registryName = block.registryName
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    @SideOnly(Side.CLIENT)
    fun registerModels() {
        for (i in 0..4) {
            ModelLoader.setCustomModelResourceLocation(this, i,
                ModelResourceLocation("${Clayium.MOD_ID}:compressed_clay_$i", "inventory")
            )
        }
    }
}