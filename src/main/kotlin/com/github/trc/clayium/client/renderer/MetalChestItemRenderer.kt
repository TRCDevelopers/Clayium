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

        MetalChestRenderer.render(
            facing = EnumFacing.SOUTH,
            material = material,
            prevLidAngle = 0f,
            lidAngle = 0f,
            x = 0.0, y = 0.0, z = 0.0,
            partialTicks = 0f, destroyStage = -1,
            alpha = 1f)
    }
}