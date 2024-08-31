package com.github.trc.clayium.common.metatileentities

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.capability.impl.RecipeLogicClayFurnace
import com.github.trc.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trc.clayium.api.metatileentity.ClayBufferMetaTileEntity
import com.github.trc.clayium.api.metatileentity.ClayInterfaceMetaTileEntity
import com.github.trc.clayium.api.metatileentity.ClayLaserMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MultiTrackBufferMetaTileEntity
import com.github.trc.clayium.api.metatileentity.SimpleMachineMetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.ClayBlastFurnaceMetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.ClayReactorMetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.LaserProxyMetaTileEntity
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.config.ConfigTierBalance
import com.github.trc.clayium.common.metatileentities.multiblock.CaReactorMetaTileEntity
import com.github.trc.clayium.common.metatileentities.multiblock.RedstoneProxyMetaTileEntity
import com.github.trc.clayium.common.recipe.registry.CRecipes
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("unused")
object MetaTileEntities {

    /**
     * Used in CA Injector
     */
    val ID_TO_MTEs = Int2ObjectAVLTreeMap<MutableList<MetaTileEntity>>()

    /* Logistics 1-200 */
    val CLAY_BUFFER = registerMetaTileEntities(1, (4..13)) { //+10
        ClayBufferMetaTileEntity(clayiumId("clay_buffer.${it.lowerName}"), it)
    }

    val MULTI_TRACK_BUFFER = registerMetaTileEntities(11, (4..13)) { //+10
        MultiTrackBufferMetaTileEntity(clayiumId("multi_track_buffer.${it.lowerName}"), it)
    }

    val STORAGE_CONTAINER = registerMetaTileEntity(101, StorageContainerMetaTileEntity(clayiumId("storage_container"), ClayTiers.AZ91D, false))
    val STORAGE_CONTAINER_UPGRADED = registerMetaTileEntity(102, StorageContainerMetaTileEntity(clayiumId("storage_container_upgraded"), ClayTiers.AZ91D, true))

    /* Singleblock Item Processing Machines 201-500 */
    val ALLOY_SMELTER = registerMetaTileEntity(201, SimpleMachineMetaTileEntity(clayiumId("alloy_smelter"), ClayTiers.PRECISION, CRecipes.ALLOY_SMELTER))

    val ASSEMBLER = registerMetaTileEntities(202, intArrayOf(3, 4, 6, 10)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("assembler.${it.lowerName}"), it, CRecipes.ASSEMBLER)
    }

    val AUTO_CLAY_CONDENSER = registerMetaTileEntities(206, intArrayOf(5, 7)) { //+2
        AutoClayCondenserMetaTileEntity(clayiumId("auto_clay_condenser.${it.lowerName}"), it)
    }

    val BENDING_MACHINE = registerMetaTileEntities(208, intArrayOf(1, 2, 3, 4, 5, 6, 7, 9)) { //+8
        SimpleMachineMetaTileEntity(clayiumId("bending_machine.${it.lowerName}"), it, CRecipes.BENDING)
    }

    val CA_INJECTOR = registerMetaTileEntities(216, (9..13)) { //+5
        CaInjectorMetaTileEntity(clayiumId("ca_injector.${it.lowerName}"), it)
    }

    val CA_CONDENSER = registerMetaTileEntities(221, (9..11)) { //+3
        CaCondenserMetaTileEntity(clayiumId("ca_condenser.${it.lowerName}"), it)
    }

    val CENTRIFUGE = registerMetaTileEntities(224, (3..6)) { //+4
        CentrifugeMetaTileEntity(clayiumId("centrifuge.${it.lowerName}"), it, it.numeric - 2)
    }

    val CHEMICAL_METAL_SEPARATOR = registerMetaTileEntity(228, ChemicalMetalSeparatorMetaTileEntity(clayiumId("chemical_metal_separator"), ClayTiers.PRECISION))

    val CHEMICAL_REACTOR = registerMetaTileEntities(229, intArrayOf(4, 5, 8)) { //+3
        SimpleMachineMetaTileEntity(clayiumId("chemical_reactor.${it.lowerName}"), it, CRecipes.CHEMICAL_REACTOR)
    }

    val CONDENSER = registerMetaTileEntities(232, intArrayOf(2, 3, 4, 5, 10)) { //+5
        SimpleMachineMetaTileEntity(clayiumId("condenser.${it.lowerName}"), it, CRecipes.CONDENSER) { mte, reg, ceHolder ->
            RecipeLogicEnergy(mte, reg, ceHolder)
                .setDurationMultiplier(ConfigTierBalance.crafting::getCraftTimeMultiplier)
                .setEnergyConsumingMultiplier(ConfigTierBalance.crafting::getConsumingEnergyMultiplier)
        }
    }

    val CUTTING_MACHINE = registerMetaTileEntities(237, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("cutting_machine.${it.lowerName}"), it, CRecipes.CUTTING_MACHINE)
    }

    val DECOMPOSER = registerMetaTileEntities(241, (2..4)) { //+3
        SimpleMachineMetaTileEntity(clayiumId("decomposer.${it.lowerName}"), it, CRecipes.DECOMPOSER)
    }

    val ELECTROLYSIS_REACTOR = registerMetaTileEntities(244, (6..9)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("electrolysis_reactor.${it.lowerName}"), it, CRecipes.ELECTROLYSIS_REACTOR)
    }

    val ENERGETIC_CLAY_CONDENSER = registerMetaTileEntities(248, (3..4)) { //+2
        SimpleMachineMetaTileEntity(clayiumId("energetic_clay_condenser.${it.lowerName}"), it, CRecipes.ENERGETIC_CLAY_CONDENSER)
    }

    // val ENERGETIC_CLAY_DECOMPOSE = registerMetaTileEntity(249, SimpleMachineMetaTileEntity(clayiumId("energetic_clay_decompose"), ClayTiers.OPA, CRecipes.ENERGETIC_CLAY_DECOMPOSE))

    val GRINDER = registerMetaTileEntities(251, intArrayOf(2, 3, 4, 5, 6, 10)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("grinder.${it.lowerName}"), it, CRecipes.GRINDER) { mte, reg, ceHolder ->
            RecipeLogicEnergy(mte, reg, ceHolder)
                .setDurationMultiplier(ConfigTierBalance.crafting::getCraftTimeMultiplier)
                .setEnergyConsumingMultiplier(ConfigTierBalance.crafting::getConsumingEnergyMultiplier)
        }
    }

    val INSCRIBER = registerMetaTileEntities(257, (3..4)) { //+2
        SimpleMachineMetaTileEntity(clayiumId("inscriber.${it.lowerName}"), it, CRecipes.INSCRIBER)
    }

    val LATHE = registerMetaTileEntities(259, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("lathe.${it.lowerName}"), it, CRecipes.LATHE)
    }

    val MATTER_TRANSFORMER = registerMetaTileEntities(263, (7..12)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("matter_transformer.${it.lowerName}"), it, CRecipes.MATTER_TRANSFORMER)
    }

    val MILLING_MACHINE = registerMetaTileEntities(269, intArrayOf(1, 3, 4)) { //+3
        SimpleMachineMetaTileEntity(clayiumId("milling_machine.${it.lowerName}"), it, CRecipes.MILLING_MACHINE)
    }

    val PIPE_DRAWING_MACHINE = registerMetaTileEntities(272, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("pipe_drawing_machine.${it.lowerName}"), it, CRecipes.PIPE_DRAWING_MACHINE)
    }

    val SMELTER = registerMetaTileEntities(276, (4..9)) { //+6
        SimpleMachineMetaTileEntity(clayiumId("smelter.${it.lowerName}"), it, CRecipes.SMELTER, ::RecipeLogicClayFurnace)
    }

    val WIRE_DRAWING_MACHINE = registerMetaTileEntities(282, (1..4)) { //+4
        SimpleMachineMetaTileEntity(clayiumId("wire_drawing_machine.${it.lowerName}"), it, CRecipes.WIRE_DRAWING_MACHINE)
    }


    /* Item Generators 501-600 */
    val CA_RESONATING_COLLECTOR = registerMetaTileEntity(501, ResonatingCollectorMetaTileEntity(clayiumId("resonating_collector"), ClayTiers.ANTIMATTER))

    val COBBLESTONE_GENERATOR = registerMetaTileEntities(502, (1..7)) { //+7
        CobblestoneGeneratorMetaTileEntity(clayiumId("cobblestone_generator.${it.lowerName}"), it)
    }

    val SALT_EXTRACTOR = registerMetaTileEntities(509, (4..7)) { //+4
        SaltExtractorMetaTileEntity(clayiumId("salt_extractor.${it.lowerName}"), it)
    }

    /* Multiblock Machines & Proxies 600-700 */
    val CLAY_INTERFACE = registerMetaTileEntities(600, (5..13)) { //+9
        ClayInterfaceMetaTileEntity(clayiumId("clay_interface.${it.lowerName}"), it)
    }

    val REDSTONE_PROXY = registerMetaTileEntities(609, (5..13)) { //+9
        RedstoneProxyMetaTileEntity(clayiumId("redstone_proxy.${it.lowerName}"), it)
    }

    val LASER_PROXY = registerMetaTileEntities(618, (7..13)) { //+7
        LaserProxyMetaTileEntity(clayiumId("laser_proxy.${it.lowerName}"), it)
    }

    val CLAY_BLAST_FURNACE = registerMetaTileEntity(625,
        ClayBlastFurnaceMetaTileEntity(clayiumId("clay_blast_furnace"), ClayTiers.PRECISION)
    )

    val CLAY_REACTOR = registerMetaTileEntity(626,
        ClayReactorMetaTileEntity(clayiumId("clay_reactor"), ClayTiers.CLAY_STEEL)
    )

    val CA_REACTOR = registerMetaTileEntities(627, (10..13)) { CaReactorMetaTileEntity(clayiumId("ca_reactor.${it.lowerName}"), it) }

    /* Clay Fabrication Machines 701-800 */
    val WATERWHEEL = registerMetaTileEntities(701, (1..2)){ //+2
        WaterwheelMetaTileEntity(clayiumId("waterwheel.${it.lowerName}"), it)
    }

    val SOLAR_CLAY_FABRICATOR = registerMetaTileEntities(703, (5..7)) { //+3
        when (it) {
            ClayTiers.ADVANCED ->
                SolarClayFabricatorMetaTileEntity(clayiumId("solar_clay_fabricator.${it.lowerName}"), it, CRecipes.SOLAR_1)
            ClayTiers.PRECISION ->
                SolarClayFabricatorMetaTileEntity(clayiumId("solar_clay_fabricator.${it.lowerName}"), it, CRecipes.SOLAR_2)
            ClayTiers.CLAY_STEEL ->
                SolarClayFabricatorMetaTileEntity(clayiumId("solar_clay_fabricator.${it.lowerName}"), it, CRecipes.SOLAR_3)
            else -> throw IllegalArgumentException()
        }
    }

    val CLAY_FABRICATOR = registerMetaTileEntities(706, intArrayOf(8, 9, 13)) {
        when (it) {
            ClayTiers.CLAYIUM ->
                ClayFabricatorMetaTileEntity(clayiumId("clay_fabricator.${it.lowerName}"), it, 11, ClayFabricatorMetaTileEntity::mk1)
            ClayTiers.ULTIMATE ->
                ClayFabricatorMetaTileEntity(clayiumId("clay_fabricator.${it.lowerName}"), it, 13, ClayFabricatorMetaTileEntity::mk2)
            ClayTiers.OPA ->
                ClayFabricatorMetaTileEntity(clayiumId("clay_fabricator.${it.lowerName}"), it, 15, ClayFabricatorMetaTileEntity::mk3)
            else -> throw IllegalArgumentException()
        }
    }

    /* Special Machines 801-1000 */
    val CLAY_LASER = registerMetaTileEntities(801, (7..10)) { //+4
        when (it) {
            ClayTiers.CLAY_STEEL -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, laserBlue = 1)
            ClayTiers.CLAYIUM -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, laserGreen = 1)
            ClayTiers.ULTIMATE -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, laserRed = 1)
            ClayTiers.ANTIMATTER -> ClayLaserMetaTileEntity(clayiumId("clay_laser.${it.lowerName}"), it, 3, 3, 3)
            else -> throw IllegalArgumentException("Invalid tier for Clay laser: (${it.lowerName}, ${it.numeric})")
        }
    }

    val PAN_CORE = registerMetaTileEntity(805, PanCoreMetaTileEntity(clayiumId("pan_core"), ClayTiers.PURE_ANTIMATTER))

    val PAN_ADAPTER = registerMetaTileEntities(806, (10..13)) { //+4
        PanAdapterMetaTileEntity(clayiumId("pan_adapter.${it.lowerName}"), it)
    }

    val PAN_DUPLICATOR = (1..10).map { //+10
        registerMetaTileEntity(810 + it, PanDuplicatorMetaTileEntity(clayiumId("pan_duplicator.${it}"), ClayTiers.PURE_ANTIMATTER, it))
    }

    /* Builder Machines 1001-1100 */
    val BLOCK_BREAKER = registerMetaTileEntity(1001,
        BlockBreakerMetaTileEntity(clayiumId("block_breaker"), ClayTiers.AZ91D))
    val RANGED_MINER = registerMetaTileEntity(1002,
        RangedMinerMetaTileEntity(clayiumId("ranged_miner"), ClayTiers.ZK60A))

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
    fun <T : MetaTileEntity> registerMetaTileEntities(startId: Int, tiers: IntArray, provider: (ClayTiers) -> T): List<T> {
        val intIds = IntArrayList()
        val mteList =  tiers.mapIndexed { i, tierNumeric ->
            val id = startId + i
            intIds.add(id)
            val iTier = ClayTiers.entries[tierNumeric]
            val metaTileEntity = provider(iTier)
            registerMetaTileEntity(id, metaTileEntity)
        }
        intIds.forEach { id -> ID_TO_MTEs.computeIfAbsent(id) { mutableListOf() }.addAll(mteList) }
        return mteList
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