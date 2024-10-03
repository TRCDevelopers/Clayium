package com.github.trc.clayium.api.metatileentity.registry

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.block.BlockMachine
import com.github.trc.clayium.api.block.ItemBlockMachine
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.registry.CRegistry
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.LoaderState

class CMteRegistry(
    val modId: String,
    val networkId: Int,
) : CRegistry<ResourceLocation, MetaTileEntity>(Short.MAX_VALUE.toInt()) {

    val blockMachine by lazy {
        if (!Loader.instance().hasReachedState(LoaderState.PREINITIALIZATION)) throw IllegalStateException("Too early to access blockMachine")
        BlockMachine().apply {
            // todo: datafix?
            registryName = if (modId == MOD_ID) clayiumId("machine") else ResourceLocation(modId, "clayium_machine")
        }
    }

    val itemBlockMachine by lazy { ItemBlockMachine(blockMachine).apply {
        registryName = if (modId == MOD_ID) clayiumId("machine") else ResourceLocation(modId, "clayium_machine")
    } }

    override fun register(id: Int, key: ResourceLocation, value: MetaTileEntity) {
        if (key.namespace != modId) {
            throw IllegalArgumentException("Cannot register a MetaTileEntity to another mod's registry")
        }
        super.register(id, key, value)
    }
}