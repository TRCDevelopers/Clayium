package com.github.trcdevelopers.clayium.common.metatileentity

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.EnumOrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class SaltExtractorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractItemGeneratorMetaTileEntity(
    metaTileEntityId, tier,
    validInputModes = energyAndNone, validOutputModes = validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.salt_extractor",
) {

    override val faceTexture = clayiumId("blocks/salt_extractor")

    override val progressPerItem: Int = 100
    override val progressPerTick = when (tier.numeric) {
        4 -> 50
        5 -> 200
        6 -> 1000
        7 -> 8000
        else -> 1
    }

    // wait for oreDict registration
    override val generatingItem by lazy { OreDictUnifier.get(OrePrefix.dust, CMaterials.salt) }

    private val clayEnergyHolder = ClayEnergyHolder(this)
    private val energyPerProgress = ClayEnergy.of(30)

    override fun createMetaTileEntity(): MetaTileEntity {
        return SaltExtractorMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun onPlacement() {
        setInput(this.frontFacing.opposite, MachineIoMode.CE)
        super.onPlacement()
    }

    override fun isTerrainValid(): Boolean {
        var waterCount = 0
        for (side in EnumFacing.entries) {
            val neighborMaterial = getNeighborBlockState(side)?.material
            if (neighborMaterial == net.minecraft.block.material.Material.WATER) {
                waterCount++
            }
            if (waterCount >= 2) return true
        }
        return false
    }

    override fun canProgress(): Boolean {
        return super.canProgress() && this.clayEnergyHolder.drawEnergy(energyPerProgress)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "salt_extractor")
    }
}