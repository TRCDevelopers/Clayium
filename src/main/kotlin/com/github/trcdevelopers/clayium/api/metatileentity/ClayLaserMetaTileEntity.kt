package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.impl.ClayLaserManager
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import kotlin.math.max
import kotlin.math.min

class ClayLaserMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: Int,
) : MetaTileEntity(metaTileEntityId, tier, listOf(MachineIoMode.NONE, MachineIoMode.CE), listOf(MachineIoMode.NONE), "machine.${CValues.MOD_ID}.clay_laser") {

    override val faceTexture = ResourceLocation(CValues.MOD_ID, "blocks/clay_laser")

    override val importItems: IItemHandlerModifiable = ItemStackHandler(0)
    override val exportItems: IItemHandlerModifiable = ItemStackHandler(0)
    override val itemInventory: IItemHandler = ItemStackHandler(0)
    override val autoIoHandler: AutoIoHandler = object : AutoIoHandler(this@ClayLaserMetaTileEntity) {
        override fun update() {}
    }

    val laserManager = ClayLaserManager(this, 3)

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

        val l = laser.getLaserLength().toDouble()

        val maxX = max(x, x + xOffset * l) + 1.0
        val minX = min(x, x + xOffset * l)
        val maxY = max(y, y + yOffset * l) + 1.0
        val minY = min(y, y + yOffset * l)
        val maxZ = max(z, z + zOffset * l) + 1.0
        val minZ = min(z, z + zOffset * l)

        AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
    }

    override fun changeIoModesOnPlacement(placer: EntityLivingBase) {
        super.changeIoModesOnPlacement(placer)
        this.frontFacing = EnumFacing.getDirectionFromEntityLiving(holder!!.pos, placer)
        this.laserManager.updateDirection(frontFacing)
    }

    override fun onPlacement() {
        val world = holder?.world ?: return
        val pos = holder?.pos ?: return
        laserManager.onPlacement(world, pos)
    }

    override fun update() {
        super.update()
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ClayLaserMetaTileEntity(metaTileEntityId, tier)
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation("${metaTileEntityId.namespace}:clay_laser", "tier=$tier"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel("aaa")
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.CAPABILITY_CLAY_LASER) {
            return ClayiumTileCapabilities.CAPABILITY_CLAY_LASER.cast(laserManager)
        }
        return super.getCapability(capability, facing)
    }

    override fun getMaxRenderDistanceSquared() = Double.POSITIVE_INFINITY
    override fun shouldRenderInPass(pass: Int) = (pass == 1)
}