package com.github.trcdevelopers.clayium.common.metatileentity

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.metatileentity.ClayBufferMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.SimpleMachineMetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object MetaTileEntities {

    val CLAY_BUFFER = registerMetaTileEntities("clay_buffer", 1, (4..13)) {
        ClayBufferMetaTileEntity(clayiumId("clay_buffer_tier$it"), it)
    }

    val BENDING_MACHINE = registerMetaTileEntities(11, intArrayOf(1, 2, 3, 4, 5, 6, 7, 9)) {
        SimpleMachineMetaTileEntity(clayiumId("bending_machine_tier$it"), it, CRecipes.BENDING)
    }

    /**
     * @param provider tier -> MetaTileEntity
     */
    fun <T : MetaTileEntity> registerMetaTileEntities(name: String, startId: Int, tiers: IntRange, provider: (Int) -> T): List<T> {
        return registerMetaTileEntities(startId, tiers.toList().toIntArray(), provider)
    }

    /**
     * @param provider tier -> MetaTileEntity
     */
    fun <T : MetaTileEntity> registerMetaTileEntities(startId: Int, tiers: IntArray, provider: (Int) -> T): List<T> {
        return tiers.mapIndexed { i, tier ->
            val id = startId + i
            val metaTileEntity = provider(tier)
            registerMetaTileEntity(id, metaTileEntity)
        }
    }

    fun <T : MetaTileEntity> registerMetaTileEntity(id: Int, sampleMetaTileEntity: T): T {
        ClayiumApi.MTE_REGISTRY.register(id, sampleMetaTileEntity.metaTileEntityId, sampleMetaTileEntity)
        return sampleMetaTileEntity
    }

    fun init() {}

    @SideOnly(Side.CLIENT)
    fun registerItemModels() {
        for (metaTileEntity in ClayiumApi.MTE_REGISTRY) {
            metaTileEntity.registerItemModel(ClayiumApi.ITEM_BLOCK_MACHINE, ClayiumApi.MTE_REGISTRY.getIDForObject(metaTileEntity))
        }
    }
}