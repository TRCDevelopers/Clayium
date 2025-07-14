package com.github.trc.clayium.client.renderer

import com.github.trc.clayium.common.blocks.ItemBlockMaterial
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

object MetalChestItemRenderer : TileEntityItemStackRenderer() {
    override fun renderByItem(itemStackIn: ItemStack) {
        val item = itemStackIn.item as? ItemBlockMaterial ?: return
        val block = item.blockMaterial
        val material = block.getCMaterial(itemStackIn)

        MetalChestRenderer.render(EnumFacing.SOUTH, material, 0f, 0f, 0.0, 0.0, 0.0, 0f, 0, 1f)
    }
}