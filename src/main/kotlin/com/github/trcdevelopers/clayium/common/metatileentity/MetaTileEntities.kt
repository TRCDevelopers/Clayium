package com.github.trcdevelopers.clayium.common.metatileentity

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.metatileentity.ClayBufferMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId

object MetaTileEntities {

    val CLAY_BUFFER = registerMetaTileEntities("clay_buffer", 1, (4..13)) {
        ClayBufferMetaTileEntity(clayiumId("clay_buffer_tier$it"), it)
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
}