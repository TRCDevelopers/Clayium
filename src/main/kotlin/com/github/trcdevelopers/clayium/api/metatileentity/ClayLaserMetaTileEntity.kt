package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.impl.ClayLaser
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

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

    val testClayLaser = ClayLaser(EnumFacing.NORTH, 1, 0, 0)

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
            return testClayLaser as T
        }
        return super.getCapability(capability, facing)
    }
}