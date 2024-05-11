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

    val CLAY_BUFFER = registerMetaTileEntities(1, (4..13)) {
        ClayBufferMetaTileEntity(clayiumId("clay_buffer_tier$it"), it)
    }

    val BENDING_MACHINE = registerMetaTileEntities(11, intArrayOf(1, 2, 3, 4, 5, 6, 7, 9)) { //+8
        SimpleMachineMetaTileEntity(clayiumId("bending_machine_tier$it"), it, CRecipes.BENDING)
    }
    val CONDENSER = registerMetaTileEntities(19, intArrayOf(2, 3, 4, 5, 10)) { //+5
        SimpleMachineMetaTileEntity(clayiumId("condenser_tier$it"), it, CRecipes.CONDENSER)
    }
    val CUTTING_MACHINE = registerMetaTileEntities(24, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("cutting_machine_tier$it"), it, CRecipes.CUTTING_MACHINE)
    }
    val DECOMPOSER = registerMetaTileEntities(28, (2..4)) { //+3
        SimpleMachineMetaTileEntity(clayiumId("decomposer_tier$it"), it, CRecipes.DECOMPOSER)
    }
    val ENERGETIC_CLAY_CONDENSER = registerMetaTileEntities(31, (3..4)) { //+2
        SimpleMachineMetaTileEntity(clayiumId("energetic_clay_condenser_tier$it"), it, CRecipes.ENERGETIC_CLAY_CONDENSER)
    }
    val GRINDER = registerMetaTileEntities(33, intArrayOf(2, 3, 4, 5, 6, 10)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("grinder_tier$it"), it, CRecipes.GRINDER)
    }
    val LATHE = registerMetaTileEntities(39, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("lathe_tier$it"), it, CRecipes.LATHE)
    }
    val MATTER_TRANSFORMER = registerMetaTileEntities(43, (7..12)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("matter_transformer_tier$it"), it, CRecipes.MATTER_TRANSFORMER)
    }
    val MILLING_MACHINE = registerMetaTileEntities(49, intArrayOf(1, 3, 4)) { //+3
        SimpleMachineMetaTileEntity(clayiumId("milling_machine_tier$it"), it, CRecipes.MILLING_MACHINE)
    }
    val PIPE_DRAWING_MACHINE = registerMetaTileEntities(52, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("pipe_drawing_machine_tier$it"), it, CRecipes.PIPE_DRAWING_MACHINE)
    }
    val WIRE_DRAWING_MACHINE = registerMetaTileEntities(56, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("wire_drawing_machine_tier$it"), it, CRecipes.WIRE_DRAWING_MACHINE)
    }

    val ASSEMBLER = registerMetaTileEntities(60, intArrayOf(3, 4, 6, 10)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("assembler_tier$it"), it, CRecipes.ASSEMBLER)
    }

    /**
     * @param provider tier -> MetaTileEntity
     */
    fun <T : MetaTileEntity> registerMetaTileEntities(startId: Int, tiers: IntRange, provider: (Int) -> T): List<T> {
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