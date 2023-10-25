package com.github.trcdeveloppers.clayium.common.blocks

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import com.github.trcdeveloppers.clayium.common.interfaces.IEnergizedClay
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems.getRarity
import com.github.trcdeveloppers.clayium.common.util.UtilLocale
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.pow

class CompressedClay private constructor(materialIn: Material, tier: Int, ce: Long, registryName: String) : Block(materialIn), IEnergizedClay {
    companion object {
        fun createBlocks(): Map<String, Block> {
            val blocks: MutableMap<String, Block> = HashMap()
            for (tier in 0..12) {
                val registryName = "compressed_clay_$tier"
                val ce = if (tier > 3) 10.toDouble().pow((tier - 4).toDouble()).toLong() else 0L
                blocks[registryName] = CompressedClay(tier, ce, registryName)
            }
            return blocks
        }
    }

    private val tier: Int
    private val ce: Long
    private val ceTooltip: String

    init {
        this.creativeTab = Clayium.CreativeTab
        this.translationKey = "$MOD_ID.$registryName"
        this.registryName = ResourceLocation(MOD_ID, registryName)
        this.blockHardness = 0.5f
        this.soundType = SoundType.GROUND
        this.lightValue = 0
        this.tier = tier
        this.ce = ce
        this.setHarvestLevel("shovel", 0)
        //todo: この10^5倍をうまくラップするようなリファクタリング
        this.ceTooltip = I18n.format("gui.clayium.energy", UtilLocale.ClayEnergyNumeral(ce * 100000))
    }

    private constructor(tier: Int, ce: Long, registryName: String) : this(Material.GROUND, tier, ce, registryName)

    override fun getClayEnergy(): Long {
        return ce
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip[0] = getRarity(tier).color.toString() + tooltip[0]
        tooltip.add(ChatFormatting.RESET.toString() + "Tier " + tier)
        tooltip.add(ceTooltip)
    }
}
