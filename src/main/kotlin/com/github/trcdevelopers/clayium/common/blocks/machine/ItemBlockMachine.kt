package com.github.trcdevelopers.clayium.common.blocks.machine

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemBlockMachine(block: BlockMachine) : ItemBlock(block) {
    init {
        setHasSubtypes(true)
        setRegistryName(block.registryName)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    @SideOnly(Side.CLIENT)
    fun registerModels() {
        for (i in (block as BlockMachine).tiers) {
            ModelLoader.setCustomModelResourceLocation(this, i,
                ModelResourceLocation("${this.registryName!!}", "tier=${i}")
            )
        }
    }
}