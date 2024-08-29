package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.ClayLaserSourceMteTrait
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntityBeacon
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import kotlin.math.pow

class ClayLaserMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val laserRed: Int = 0,
    private val laserGreen: Int = 0,
    private val laserBlue: Int = 0,
) : MetaTileEntity(
    metaTileEntityId, tier,
    validInputModesLists[0], validOutputModesLists[0],
    "machine.${CValues.MOD_ID}.clay_laser",
) {
    override val faceTexture = ResourceLocation(CValues.MOD_ID, "blocks/clay_laser")

    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val itemInventory: IItemHandler = EmptyItemStackHandler

    private val clayEnergyHolder = ClayEnergyHolder(this)
    val energyCost = ClayEnergy.milli(
        when (tier.numeric) {
            in 7..10 -> 400 * 10.toDouble().pow((tier.numeric - 7).toDouble())
            else -> 400
        }.toLong()
    )

    val laserManager = ClayLaserSourceMteTrait(this, laserRed, laserGreen, laserBlue)
    private var canActivateByRedstone = false

//    @field:SideOnly(Side.CLIENT)
//    @get:SideOnly(Side.CLIENT)
    override val renderBoundingBox = TileEntityBeacon.INFINITE_EXTENT_AABB

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return true
    }

    override fun onPlacement() {
        this.setInput(this.frontFacing.opposite, MachineIoMode.CE)
        super.onPlacement()
    }

    override fun update() {
        super.update()
        if (isRemote) return
        refreshRedstone()
        if (canActivateByRedstone) {
            this.laserManager.isIrradiating = clayEnergyHolder.drawEnergy(energyCost, simulate = false)
        } else {
            this.laserManager.isIrradiating = false
        }
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("clay_laser_tier$tier", 176, 32 + 94)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager)
                    .child(clayEnergyHolder.createCeTextWidget(syncManager)
                        .bottom(12).left(0))
                )
            }
    }

    override val useGlobalRenderer: Boolean = true
    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared() = Double.POSITIVE_INFINITY
    @SideOnly(Side.CLIENT)
    override fun shouldRenderInPass(pass: Int) = (pass == 1)

    private fun refreshRedstone() {
        val pos = this.pos ?: return
        val world = this.world ?: return
        // default->isNotPowered, inverted->isPowered
        canActivateByRedstone = world.isBlockPowered(pos) == ConfigCore.misc.invertClayLaserRsCondition
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayLaserMetaTileEntity(metaTileEntityId, tier, laserRed, laserGreen, laserBlue)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            ModelResourceLocation(clayiumId("clay_laser"), "tier=${tier.numeric}"))
    }
}