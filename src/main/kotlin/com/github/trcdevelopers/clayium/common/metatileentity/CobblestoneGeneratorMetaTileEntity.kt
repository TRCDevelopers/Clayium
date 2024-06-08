package com.github.trcdevelopers.clayium.common.metatileentity

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class CobblestoneGeneratorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractItemGeneratorMetaTileEntity(
    metaTileEntityId, tier,
    validInputModes = onlyNoneList, validOutputModes = validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.cobblestone_generator",
) {

    override val faceTexture = clayiumId("blocks/cobblestone_generator")

    override val generatingItem: ItemStack = ItemStack(Blocks.COBBLESTONE)
    override val progressPerItem = 100
    override val progressPerTick = when (tier.numeric) {
        1 -> 2
        2 -> 5
        3 -> 15
        4 -> 50
        5 -> 200
        6 -> 1000
        7 -> 8000
        else -> 1
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return CobblestoneGeneratorMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun isTerrainValid(): Boolean {
        var hasWaterNeighbor = false
        var hasLavaNeighbor = false
        for (side in EnumFacing.entries) {
            val neighborMaterial = getNeighborBlockState(side)?.material
            hasWaterNeighbor = hasWaterNeighbor || neighborMaterial == Material.WATER
            hasLavaNeighbor = hasLavaNeighbor || neighborMaterial == Material.LAVA

            if (hasWaterNeighbor && hasLavaNeighbor) return true
        }
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("cobblestone_generator"), "tier=${tier.lowerName}"))
    }
}