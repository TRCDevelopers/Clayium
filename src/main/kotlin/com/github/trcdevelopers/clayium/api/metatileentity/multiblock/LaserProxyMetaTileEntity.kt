package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

class LaserProxyMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, onlyNoneList, onlyNoneList, "machine.${CValues.MOD_ID}.laser_proxy") {

    override fun onLink(target: MetaTileEntity) {
        TODO("Not yet implemented")
    }

    override fun onUnlink() {
        TODO("Not yet implemented")
    }

    override var importItems: IItemHandlerModifiable = ItemStackHandler(0)
    override var exportItems: IItemHandlerModifiable = ItemStackHandler(0)
    override var itemInventory: IItemHandler = ItemStackHandler(0)
    override var autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    override fun createMetaTileEntity(): MetaTileEntity {
        return LaserProxyMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("laser_proxy"), "tier=${tier.lowerName}"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        TODO("Not yet implemented")
    }
}