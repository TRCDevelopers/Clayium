package com.github.trcdevelopers.clayium.common.blocks.clay

import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
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

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return I18n.format("tile.${Clayium.MOD_ID}.compressed_clay_tier${stack.metadata}.name")
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("tooltip.${Clayium.MOD_ID}.tier", stack.metadata))
        tooltip.add(I18n.format("tooltip.${Clayium.MOD_ID}.ce", 0))
    }

    @SideOnly(Side.CLIENT)
    fun registerModels() {
        for (i in 0..3) {
            ModelLoader.setCustomModelResourceLocation(this, i,
                ModelResourceLocation("${Clayium.MOD_ID}:compressed_clay_$i", "inventory")
            )
        }
    }
}