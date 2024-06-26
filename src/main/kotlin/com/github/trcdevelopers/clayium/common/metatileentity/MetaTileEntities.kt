package com.github.trcdevelopers.clayium.common.metatileentity

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicClayFurnace
import com.github.trcdevelopers.clayium.api.metatileentity.ClayBufferMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.ClayInterfaceMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.ClayLaserMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.SimpleMachineMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.ClayBlastFurnaceMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.ClayReactorMetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.LaserProxyMetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.metatileentity.multiblock.RedstoneProxyMetaTileEntity
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("unused")
object MetaTileEntities {

    val CLAY_BUFFER = registerMetaTileEntities(1, (4..13)) {
        ClayBufferMetaTileEntity(clayiumId("clay_buffer.${it.lowerName}"), it)
    }

    val BENDING_MACHINE = registerMetaTileEntities(11, intArrayOf(1, 2, 3, 4, 5, 6, 7, 9)) { //+8
        SimpleMachineMetaTileEntity(clayiumId("bending_machine.${it.lowerName}"), it, CRecipes.BENDING)
    }
    val CONDENSER = registerMetaTileEntities(19, intArrayOf(2, 3, 4, 5, 10)) { //+5
        SimpleMachineMetaTileEntity(clayiumId("condenser.${it.lowerName}"), it, CRecipes.CONDENSER)
    }
    val CUTTING_MACHINE = registerMetaTileEntities(24, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("cutting_machine.${it.lowerName}"), it, CRecipes.CUTTING_MACHINE)
    }
    val DECOMPOSER = registerMetaTileEntities(28, (2..4)) { //+3
        SimpleMachineMetaTileEntity(clayiumId("decomposer.${it.lowerName}"), it, CRecipes.DECOMPOSER)
    }
    val ENERGETIC_CLAY_CONDENSER = registerMetaTileEntities(31, (3..4)) { //+2
        SimpleMachineMetaTileEntity(clayiumId("energetic_clay_condenser.${it.lowerName}"), it, CRecipes.ENERGETIC_CLAY_CONDENSER)
    }
    val GRINDER = registerMetaTileEntities(33, intArrayOf(2, 3, 4, 5, 6, 10)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("grinder.${it.lowerName}"), it, CRecipes.GRINDER)
    }
    val LATHE = registerMetaTileEntities(39, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("lathe.${it.lowerName}"), it, CRecipes.LATHE)
    }
    val MATTER_TRANSFORMER = registerMetaTileEntities(43, (7..12)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("matter_transformer.${it.lowerName}"), it, CRecipes.MATTER_TRANSFORMER)
    }
    val MILLING_MACHINE = registerMetaTileEntities(49, intArrayOf(1, 3, 4)) { //+3
        SimpleMachineMetaTileEntity(clayiumId("milling_machine.${it.lowerName}"), it, CRecipes.MILLING_MACHINE)
    }
    val PIPE_DRAWING_MACHINE = registerMetaTileEntities(52, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("pipe_drawing_machine.${it.lowerName}"), it, CRecipes.PIPE_DRAWING_MACHINE)
    }
    val WIRE_DRAWING_MACHINE = registerMetaTileEntities(56, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("wire_drawing_machine.${it.lowerName}"), it, CRecipes.WIRE_DRAWING_MACHINE)
    }
    val SMELTER = registerMetaTileEntities(60, (4..9)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("smelter.${it.lowerName}"), it, CRecipes.SMELTER, ::RecipeLogicClayFurnace)
    }

    val ASSEMBLER = registerMetaTileEntities(66, intArrayOf(3, 4, 6, 10)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("assembler.${it.lowerName}"), it, CRecipes.ASSEMBLER)
    }

    val CLAY_LASER = registerMetaTileEntities(70, (7..10)) { //+4
        when (it) {
            ClayTiers.CLAY_STEEL -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, laserBlue = 1)
            ClayTiers.CLAYIUM -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, laserGreen = 1)
            ClayTiers.ULTIMATE -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, laserRed = 1)
            ClayTiers.ANTIMATTER -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, 3, 3, 3)
            else -> throw IllegalArgumentException("Invalid tier for Clay laser: (${it.lowerName}, ${it.numeric})")
        }
    }

    val SOLAR_CLAY_FABRICATOR = registerMetaTileEntities(74, (5..7)) {
        SolarClayFabricatorMetaTileEntity(clayiumId("solar_clay_fabricator.${it.lowerName}"), it)
    }

    val COBBLESTONE_GENERATOR = registerMetaTileEntities(77, (1..7)) {
        CobblestoneGeneratorMetaTileEntity(clayiumId("cobblestone_generator.${it.lowerName}"), it)
    }

    val SALT_EXTRACTOR = registerMetaTileEntities(84, (4..7)) {
        SaltExtractorMetaTileEntity(clayiumId("salt_extractor.${it.lowerName}"), it)
    }


    val CLAY_INTERFACE = registerMetaTileEntities(123, (5..13)) {
        ClayInterfaceMetaTileEntity(clayiumId("clay_interface.${it.lowerName}"), it)
    }

    val REDSTONE_PROXY = registerMetaTileEntities(133, (5..13)) {
        RedstoneProxyMetaTileEntity(clayiumId("redstone_proxy.${it.lowerName}"), it)
    }

    val LASER_PROXY = registerMetaTileEntities(143, (7..13)) {
        LaserProxyMetaTileEntity(clayiumId("laser_proxy.${it.lowerName}"), it)
    }

    val CLAY_BLAST_FURNACE = registerMetaTileEntity(150,
        ClayBlastFurnaceMetaTileEntity(clayiumId("clay_blast_furnace"), ClayTiers.PRECISION)
    )

    val CLAY_REACTOR = registerMetaTileEntity(151,
        ClayReactorMetaTileEntity(clayiumId("clay_reactor"), ClayTiers.CLAY_STEEL)
    )

    val STORAGE_CONTAINER = registerMetaTileEntity(152, StorageContainerMetaTileEntity(clayiumId("storage_container"), ClayTiers.AZ91D, false))
    val STORAGE_CONTAINER_UPGRADED = registerMetaTileEntity(153, StorageContainerMetaTileEntity(clayiumId("storage_container_upgraded"), ClayTiers.AZ91D, true))

    val CA_RESONATING_COLLECTOR = registerMetaTileEntity(154, ResonatingCollectorMetaTileEntity(clayiumId("resonating_collector"), ClayTiers.ANTIMATTER))

    val CA_INJECTOR = registerMetaTileEntities(155, (9..13)) {
        CaInjectorMetaTileEntity(clayiumId("ca_injector.${it.lowerName}"), it)
    }

    val CA_CONDENSER = registerMetaTileEntities(160, (9..11)) {
        CaCondenserMetaTileEntity(clayiumId("ca_condenser.${it.lowerName}"), it)
    }

    /**
     * @param tiers corresponding to the main material tiers (Clay, DenseClay...OPA)
     * @param provider tier -> MetaTileEntity
     */
    fun <T : MetaTileEntity> registerMetaTileEntities(startId: Int, tiers: IntRange, provider: (ITier) -> T): List<T> {
        return registerMetaTileEntities(startId, tiers.toList().toIntArray(), provider)
    }

    /**
     * @param tiers corresponding to the main material tiers (Clay, DenseClay...OPA)
     * @param provider tier -> MetaTileEntity
     */
    fun <T : MetaTileEntity> registerMetaTileEntities(startId: Int, tiers: IntArray, provider: (ITier) -> T): List<T> {
        return tiers.mapIndexed { i, tierNumeric ->
            val id = startId + i
            val iTier = ClayTiers.entries[tierNumeric]
            val metaTileEntity = provider(iTier)
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