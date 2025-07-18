package com.github.trc.clayium.client.renderer

import com.github.trc.clayium.common.items.ItemBlockMetalChest
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

object MetalChestItemRenderer : TileEntityItemStackRenderer() {
    override fun renderByItem(itemStackIn: ItemStack) {
        val item = itemStackIn.item as? ItemBlockMetalChest ?: return
        val block = item.blockMetalChest
        val material = block.getCMaterial(itemStackIn)

        MetalChestRenderer.render(EnumFacing.SOUTH, material, 0f, 0f, 0.0, 0.0, 0.0, 0f, 0, 1f)
    }
}