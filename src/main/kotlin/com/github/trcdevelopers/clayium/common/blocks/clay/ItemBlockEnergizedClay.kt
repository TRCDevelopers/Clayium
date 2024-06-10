package com.github.trcdevelopers.clayium.common.blocks.clay

import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.clayenergy.IEnergizedClayItem
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.IRarity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.pow

class ItemBlockEnergizedClay(block: BlockEnergizedClay) : ItemBlock(block), IEnergizedClayItem {

    init {
        hasSubtypes = true
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return I18n.format("tile.${Clayium.MOD_ID}.compressed_clay_tier${stack.metadata + 4}.name")
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("tooltip.${Clayium.MOD_ID}.tier", getTier(stack).numeric))
        tooltip.add(I18n.format("tooltip.${Clayium.MOD_ID}.ce", ClayEnergy.of(10.toDouble().pow(stack.metadata).toLong()).format()))
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return getTier(stack).rarity
    }

    override fun getClayEnergy(stack: ItemStack): ClayEnergy {
        return ClayEnergy.of(10.toDouble().pow(stack.metadata).toLong())
    }

    override fun getTier(stack: ItemStack): ITier {
        return ClayTiers.entries[stack.metadata + 4]
    }
}