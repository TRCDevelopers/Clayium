package com.github.trc.clayium.client.renderer

import com.github.trc.clayium.common.blocks.ItemBlockMaterial
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

object MetalChestItemRenderer : TileEntityItemStackRenderer() {
    override fun renderByItem(itemStackIn: ItemStack) {
        val item = itemStackIn.item as? ItemBlockMaterial ?: return
        val block = item.blockMaterial
        val material = block.getCMaterial(itemStackIn)

        GlStateManager.pushMatrix()
//        GlStateManager.rotate(30f, 1f, 0f, 0f)
//        GlStateManager.rotate(45f, 0f, 1f, 0f)
//        GlStateManager.translate(0.0, 0.25, 0.0)
//        GlStateManager.scale(0.65f, 0.65f, 0.65f)
        MetalChestRenderer.render(EnumFacing.NORTH, material, 0f, 0f, 0.0, 0.0, 0.0, 0f, 0, 1f)
        GlStateManager.popMatrix()
    }
}