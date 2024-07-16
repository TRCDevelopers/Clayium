package com.github.trc.clayium.api.metatileentity

import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.util.ITier
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

abstract class AbstractClayFabricatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    translationKey: String,
    private val maxTierNum: Int,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], translationKey) {

    override val importItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, false)
    override val exportItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, true)
    override val itemInventory: IItemHandler = ItemHandlerProxy(importItems, exportItems)
    val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

}