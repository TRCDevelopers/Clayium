package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.impl.ClayLaserSource
import com.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.config.ConfigCore
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import kotlin.math.max
import kotlin.math.min

class ClayLaserMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val laserRed: Int = 0,
    private val laserGreen: Int = 0,
    private val laserBlue: Int = 0,
) : MetaTileEntity(
    metaTileEntityId, tier,
    validInputModesLists[0], validOutputModesLists[0],
    "machine.${CValues.MOD_ID}.clay_laser.${tier.lowerName}",
) {

    override val faceTexture = ResourceLocation(CValues.MOD_ID, "blocks/clay_laser")

    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val itemInventory: IItemHandler = EmptyItemStackHandler
    override val autoIoHandler: AutoIoHandler = object : AutoIoHandler(this@ClayLaserMetaTileEntity) {
        override fun update() {}
    }

    val laserManager = ClayLaserSource(this, laserRed, laserGreen, laserBlue)

    override val renderBoundingBox by lazy {
        val laser = laserManager.laser
        val pos = holder?.pos ?: return@lazy null
        val x = pos.x.toDouble()
        val y = pos.y.toDouble()
        val z = pos.z.toDouble()

        val direction = laser.laserDirection
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
        this.updateLaserActivation()
    }

    override fun onRemoval() {
        laserManager.onRemoval()
    }

    override fun onNeighborChanged(facing: EnumFacing) {
        super.onNeighborChanged(facing)
        this.updateLaserActivation()
    }

    override fun neighborChanged() {
        this.updateLaserActivation()
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayLaserMetaTileEntity(metaTileEntityId, tier, laserRed, laserGreen, laserBlue)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation("${metaTileEntityId.namespace}:clay_laser", "tier=${tier.numeric}"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("clay_laser_tier$tier", 176, 32 + 94)
            .child(IKey.lang("machine.clayium.clay_laser.${tier.lowerName}", IKey.lang(tier.prefixTranslationKey)).asWidget()
                .top(6)
                .left(6))
            .bindPlayerInventory()
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.CAPABILITY_CLAY_LASER) {
            return ClayiumTileCapabilities.CAPABILITY_CLAY_LASER.cast(laserManager)
        }
        return super.getCapability(capability, facing)
    }

    override fun getMaxRenderDistanceSquared() = Double.POSITIVE_INFINITY
    override fun shouldRenderInPass(pass: Int) = (pass == 1)

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        // add the tier-specific tooltip first
        UtilLocale.formatTooltips(tooltip, "machine.clayium.${metaTileEntityId.path}.tooltip")
        // then add the machine-specific tooltip
        UtilLocale.formatTooltips(tooltip, "machine.clayium.clay_laser.tooltip")
    }

    private fun updateLaserActivation() {
        // default->isNotPowered, inverted->isPowered
        laserManager.isActive = (world?.isBlockPowered(this.pos ?: return) == ConfigCore.misc.invertClayLaserRsCondition)
    }
}