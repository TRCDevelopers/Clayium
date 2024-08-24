package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.capability.impl.ClayLaserSource
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import kotlin.math.max
import kotlin.math.min
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

    val laserManager = ClayLaserSource(this, laserRed, laserGreen, laserBlue)
    private var canActivateByRedstone = false

    override val renderBoundingBox by lazy {
        val laser = laserManager.laser
        val pos = holder?.pos ?: return@lazy null
        val x = pos.x.toDouble()
        val y = pos.y.toDouble()
        val z = pos.z.toDouble()

        val direction = laser.direction
        val xOffset = direction.xOffset.toDouble()
        val yOffset = direction.yOffset.toDouble()
        val zOffset = direction.zOffset.toDouble()

        val l = laserManager.laserLength.toDouble()

        val maxX = max(x, x + xOffset * l) + 1.0
        val minX = min(x, x + xOffset * l)
        val maxY = max(y, y + yOffset * l) + 1.0
        val minY = min(y, y + yOffset * l)
        val maxZ = max(z, z + zOffset * l) + 1.0
        val minZ = min(z, z + zOffset * l)

        AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
    }

    override fun onPlacement() {
        super.onPlacement()
        laserManager.updateDirection(this.frontFacing)
    }

    override fun onNeighborChanged(facing: EnumFacing) {
        super.onNeighborChanged(facing)
        refreshRedstone()
    }

    override fun neighborChanged() {
        super.neighborChanged()
        refreshRedstone()
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayLaserMetaTileEntity(metaTileEntityId, tier, laserRed, laserGreen, laserBlue)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(
            item,
            meta,
            ModelResourceLocation("${metaTileEntityId.namespace}:clay_laser", "tier=${tier.numeric}")
        )
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("clay_laser_tier$tier", 176, 32 + 94)
            .child(mainColumn {
                child(buildMainParentWidget(syncManager)
                    .child(clayEnergyHolder.createCeTextWidget(syncManager)
                        .bottom(12).left(0))
                )
            })
    }

    override fun getMaxRenderDistanceSquared() = Double.POSITIVE_INFINITY
    override fun shouldRenderInPass(pass: Int) = (pass == 1)

    private fun refreshRedstone() {
        val pos = this.pos ?: return
        val world = this.world ?: return
        // default->isNotPowered, inverted->isPowered
        canActivateByRedstone = world.isBlockPowered(pos) == ConfigCore.misc.invertClayLaserRsCondition
    }

    override fun update() {
        super.update()
        if (isRemote) return
        if (canActivateByRedstone) {
            laserManager.isActive = clayEnergyHolder.drawEnergy(energyCost, simulate = false)
        } else {
            laserManager.isActive = false
        }
    }
}