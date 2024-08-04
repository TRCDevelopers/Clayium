package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.ClayTiers.*
import com.github.trc.clayium.common.Clayium
import com.github.trc.clayium.common.blocks.ClayiumBlocks.MACHINE_HULL
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.items.metaitem.MetaItemClayium
import com.github.trc.clayium.common.metatileentity.MetaTileEntities
import com.github.trc.clayium.common.recipe.RecipeUtils
import com.github.trc.clayium.common.recipe.builder.RecipeBuilder
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix
import com.github.trc.clayium.common.unification.stack.UnificationEntry
import kotlin.math.pow

object MachineBlockRecipeLoader {
    fun registerRecipes() {
        val assembler = CRecipes.ASSEMBLER

        //region Hulls
        val mainHullMaterials = listOf(
            CMaterials.clay,
            CMaterials.clay,
            CMaterials.denseClay,
            CMaterials.industrialClay,
            CMaterials.advancedIndustrialClay,
            CMaterials.impureSilicon,
            CMaterials.aluminum,
            CMaterials.claySteel,
            CMaterials.clayium,
            CMaterials.ultimateAlloy,
            CMaterials.antimatter,
            CMaterials.pureAntimatter,
            CMaterials.octupleEnergyClay,
            CMaterials.octuplePureAntimatter,
        )

        val circuits: List<Any> = listOf(
            Unit, // not used, but needed for indexing
            UnificationEntry(OrePrefix.gear, CMaterials.clay),
            MetaItemClayParts.CLAY_CIRCUIT,
            MetaItemClayParts.SIMPLE_CIRCUIT,
            MetaItemClayParts.BASIC_CIRCUIT,
            MetaItemClayParts.ADVANCED_CIRCUIT,
            MetaItemClayParts.PRECISION_CIRCUIT,
            MetaItemClayParts.INTEGRATED_CIRCUIT,
            MetaItemClayParts.CLAY_CORE,
            MetaItemClayParts.CLAY_BRAIN,
            MetaItemClayParts.CLAY_SPIRIT,
            MetaItemClayParts.CLAY_SOUL,
            MetaItemClayParts.CLAY_ANIMA,
            MetaItemClayParts.CLAY_PSYCHE,
        )

        for (tier in ClayTiers.entries) {
            val hull = MACHINE_HULL
            val i = tier.numeric
            when (tier) {
                DEFAULT -> RecipeUtils.addShapedRecipe("raw_clay_machine_hull", hull.getItem(tier),
                    "PPP", "PGP", "PPP",
                    'P', UnificationEntry(OrePrefix.largePlate, CMaterials.clay),
                    'G', UnificationEntry(OrePrefix.gear, CMaterials.clay))
                CLAY -> RecipeUtils.addSmeltingRecipe(hull.getItem(DEFAULT), hull.getItem(CLAY))
                DENSE_CLAY ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PPP", "PCP", "PPP",
                        'C', circuits[i],
                        'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i])
                    )
                SIMPLE, BASIC, PRECISION, CLAY_STEEL, CLAYIUM,
                ULTIMATE, ANTIMATTER, PURE_ANTIMATTER, OEC, OPA ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PEP", "PCP", "PPP",
                        'E', MetaItemClayParts.CEE,
                        'C', circuits[i],
                        'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i])
                    )
                ADVANCED ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PEP", "SCS", "PSP",
                        'E', MetaItemClayParts.CEE,
                        'C', circuits[i],
                        'S', UnificationEntry(OrePrefix.largePlate, CMaterials.silicone),
                        'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i])
                    )
                AZ91D -> CRecipes.ASSEMBLER.builder()
                    .input(OrePrefix.largePlate, CMaterials.az91d)
                    .input(MetaItemClayParts.PRECISION_CIRCUIT)
                    .output(hull.getItem(tier))
                    .tier(4).CEt(ClayEnergy.milli(100)).duration(120)
                    .buildAndRegister()
                ZK60A ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PPP", "PCP", "PPP",
                        'C', MetaItemClayParts.PRECISION_CIRCUIT,
                        'P', UnificationEntry(OrePrefix.largePlate, CMaterials.zk60a)
                    )
            }
        }
        //endregion

        registerMachineRecipeHull(MetaTileEntities.BENDING_MACHINE) {
            input(OrePrefix.plate, CMaterials.denseClay, 3)
        }
        registerLowTierRecipe(MetaTileEntities.BENDING_MACHINE, "Sgp", "pHp", "Sgp")
        registerMachineRecipeHull(MetaTileEntities.MILLING_MACHINE) {
            input(OrePrefix.cuttingHead, CMaterials.denseClay)
        }
        registerLowTierRecipe(MetaTileEntities.MILLING_MACHINE, "pCp", "MHM", "pgp")
        registerMachineRecipeHull(MetaTileEntities.ENERGETIC_CLAY_CONDENSER) {
            input(MetaItemClayParts.CEE, 2)
        }
        registerLowTierRecipe(MetaTileEntities.ENERGETIC_CLAY_CONDENSER, "pgp", "EHE", "pcp",
            'E', MetaItemClayParts.CEE)
        registerMachineRecipeHull(MetaTileEntities.WIRE_DRAWING_MACHINE) {
            input(OrePrefix.pipe, CMaterials.denseClay)
        }
        registerLowTierRecipe(MetaTileEntities.WIRE_DRAWING_MACHINE, "gMg", "OHO", "gMg")
        registerMachineRecipeHull(MetaTileEntities.PIPE_DRAWING_MACHINE) {
            input(OrePrefix.cylinder, CMaterials.denseClay)
        }
        registerLowTierRecipe(MetaTileEntities.PIPE_DRAWING_MACHINE, "gMg", "IHO", "gMg")
        registerMachineRecipeHull(MetaTileEntities.CUTTING_MACHINE) {
            input(OrePrefix.cuttingHead, CMaterials.clay)
        }
        registerLowTierRecipe(MetaTileEntities.CUTTING_MACHINE, "pgp", "MHC", "pgp")
        registerMachineRecipeHull(MetaTileEntities.LATHE) {
            input(OrePrefix.spindle, CMaterials.clay)
        }
        registerLowTierRecipe(MetaTileEntities.LATHE, "pgp", "SHM", "pgp")
        registerMachineRecipeHull(MetaTileEntities.CONDENSER) {
            input(OrePrefix.largePlate, CMaterials.denseClay)
        }
        registerLowTierRecipe(MetaTileEntities.CONDENSER, "gpg", "pHp", "gpg")
        registerMachineRecipeHull(MetaTileEntities.GRINDER) {
            input(OrePrefix.grindingHead, CMaterials.denseClay)
        }
        registerLowTierRecipe(MetaTileEntities.GRINDER, "pGp", "MHM", "pgp")
        registerMachineRecipeHull(MetaTileEntities.DECOMPOSER) {
            input(OrePrefix.gear, CMaterials.clay, 4)
        }
        registerLowTierRecipe(MetaTileEntities.DECOMPOSER, "gMg", "cHc", "gOg")
        registerMachineRecipeHull(MetaTileEntities.ASSEMBLER) {
            input(OrePrefix.gear, CMaterials.denseClay)
        }
        registerLowTierRecipe(MetaTileEntities.ASSEMBLER, "gcg", "MHM", "gcg")
        registerMachineRecipeHull(MetaTileEntities.CENTRIFUGE) {
            input(OrePrefix.spindle, CMaterials.denseClay)
        }
        registerLowTierRecipe(MetaTileEntities.CENTRIFUGE, "gMg", "MHM", "gMg")
        registerMachineRecipeHull(MetaTileEntities.CHEMICAL_REACTOR) {
            input(MetaItemClayParts.BASIC_CIRCUIT)
        }
        //AutomaticClayCondenser, Buffer+AdvancedCircuit
        registerMachineRecipeHull(MetaTileEntities.SMELTER) {
            input(MetaItemClayParts.SIMPLE_CIRCUIT)
        }
        registerMachineRecipeHull(MetaTileEntities.SOLAR_CLAY_FABRICATOR) {
            input(OrePrefix.plate, CMaterials.silicon, 16)
        }
        clayBuffers()
        /* Machine Proxy */
        for (metaTileEntity in MetaTileEntities.CLAY_INTERFACE) {
            CRecipes.ASSEMBLER.builder()
                .input(MACHINE_HULL.getItem(metaTileEntity.tier as ClayTiers))
                .input(MetaTileEntities.CLAY_BUFFER[metaTileEntity.tier.numeric - 4])
                .output(metaTileEntity.getStackForm())
                .tier(4).CEt(ClayEnergy(10.0.pow(metaTileEntity.tier.numeric - 3).toLong())).duration(40)
                .buildAndRegister()
        }
        /* Redstone Proxy */
        for (metaTileEntity in MetaTileEntities.REDSTONE_PROXY) {
            CRecipes.ASSEMBLER.builder()
                .input(MetaTileEntities.REDSTONE_PROXY[metaTileEntity.tier.numeric - 5])
                .input(MetaItemClayParts.EnergizedClayDust, 16)
                .output(metaTileEntity.getStackForm())
                .tier(4).CEt(ClayEnergy(10.0.pow(metaTileEntity.tier.numeric - 3).toLong())).duration(40)
                .buildAndRegister()
        }
        registerMachineRecipeBuffer(MetaTileEntities.LASER_PROXY) {
            input(MetaItemClayParts.LaserParts)
        }
        registerMachineRecipeHull(MetaTileEntities.CLAY_LASER) {
            input(MetaItemClayParts.LaserParts, 4).duration(480)
        }
        /* CA Resonating Collector */
        CRecipes.CA_INJECTOR.builder()
            .input(MACHINE_HULL.getItem(ANTIMATTER))
            .input(OrePrefix.gem, CMaterials.antimatter, 8)
            .output(MetaTileEntities.CA_RESONATING_COLLECTOR)
            .tier(10).CEt(2.0).duration(4000)
            .buildAndRegister()
        /* CA Injector */
        assembler.builder()
            .input(MACHINE_HULL.getItem(ULTIMATE))
            .input(MetaTileEntities.CLAY_REACTOR, 16)
            .output(MetaTileEntities.CA_INJECTOR[0])
            .tier(6).CEt(ClayEnergy.of(100)).duration(480)
            .buildAndRegister()
        for (i in 1..3) {
            val mte = MetaTileEntities.CA_INJECTOR[i]
            assembler.builder()
                .input(MACHINE_HULL.getItem(mte.tier as ClayTiers))
                .input(MetaTileEntities.CA_INJECTOR[i - 1], 2)
                .output(mte)
                .tier(10).CEt(ClayEnergy.of(100 * 10.0.pow(i).toLong())).duration(480)
                .buildAndRegister()
        }
        caCondenser()

        /* Cobblestone Generator */
        for ((i, m) in listOf(CMaterials.clay, CMaterials.denseClay, CMaterials.industrialClay).withIndex()) {
            assembler.builder()
                .input(OrePrefix.largePlate, m)
                .input(MetaItemClayParts.SIMPLE_CIRCUIT)
                .output(MetaTileEntities.COBBLESTONE_GENERATOR[i])
                .tier(4).duration(40)
                .buildAndRegister()
        }
        registerMachineRecipeBuffer(MetaTileEntities.COBBLESTONE_GENERATOR) {
            input(MetaItemClayParts.SIMPLE_CIRCUIT)
        }
        registerLowTierRecipe(MetaTileEntities.COBBLESTONE_GENERATOR, " g ", "OHO", " g ")
        registerMachineRecipeBuffer(MetaTileEntities.SALT_EXTRACTOR) {
            input(MetaItemClayParts.SIMPLE_CIRCUIT)
        }

        /* Storage Container */
        assembler.builder()
            .input(MACHINE_HULL.getItem(AZ91D, 4))
            .input(MetaTileEntities.CLAY_INTERFACE[0])
            .output(MetaTileEntities.STORAGE_CONTAINER, 4)
            .tier(4).CEt(ClayEnergy.milli(100)).duration(120)
            .buildAndRegister()
        RecipeUtils.addShapelessRecipe("upgrade_storage_container", MetaTileEntities.STORAGE_CONTAINER_UPGRADED.getStackForm(),
            MetaTileEntities.STORAGE_CONTAINER, MetaItemClayParts.CLAY_CORE)

        /* Clay Blast Furnace */
        assembler.builder()
            .input(MetaTileEntities.SMELTER[2])
            .input(MetaTileEntities.CLAY_INTERFACE[1])
            .output(MetaTileEntities.CLAY_BLAST_FURNACE)
            .tier(4).CEt(ClayEnergy.milli(100)).duration(120)
            .buildAndRegister()
        /* Clay Reactor */
        assembler.builder()
            .input(MACHINE_HULL.getItem(CLAY_STEEL))
            .input(MetaTileEntities.LASER_PROXY[0])
            .output(MetaTileEntities.CLAY_REACTOR)
            .tier(6).CEt(ClayEnergy.of(1)).duration(1200)
            .buildAndRegister()
        /* Waterwheel */
        val wheelHulls = listOf(MACHINE_HULL.getItem(CLAY), MACHINE_HULL.getItem(DENSE_CLAY))
        val wheels = listOf(UnificationEntry(OrePrefix.wheel, CMaterials.clay), UnificationEntry(OrePrefix.wheel, CMaterials.denseClay))
        for ((i, e) in wheelHulls.zip(wheels).withIndex()) {
            val (block, item) = e
            RecipeUtils.addShapelessRecipe("${CValues.MOD_ID}.waterwheel_$i",
                MetaTileEntities.WATERWHEEL[i].getStackForm(), block, item)
        }
        /* Chemical Metal Separator */
        assembler.builder()
            .input(MetaTileEntities.CHEMICAL_REACTOR[1])
            .input(MetaTileEntities.SMELTER[2])
            .output(MetaTileEntities.CHEMICAL_METAL_SEPARATOR)
            .tier(4).CEt(ClayEnergy.milli(100)).duration(40)
            .buildAndRegister()
        /* Electrolysis Reactor */
        for (mte in MetaTileEntities.ELECTROLYSIS_REACTOR) {
            assembler.builder()
                .input(MetaTileEntities.CHEMICAL_REACTOR[1])
                .input(circuits[mte.tier.numeric] as MetaItemClayium.MetaValueItem)
                .output(mte)
                .tier(4).CEt(ClayEnergy.milli(100)).duration(40)
                .buildAndRegister()
        }
        /* Matter Transformer */
        for ((i, mte) in MetaTileEntities.MATTER_TRANSFORMER.withIndex()) {
            if (i in 0..2) {
                assembler.builder()
                    .input(MetaTileEntities.CLAY_REACTOR)
                    .input(MetaTileEntities.ELECTROLYSIS_REACTOR[i + 1])
                    .output(mte)
                    .tier(6)
                    .CEt(ClayEnergy.of(10.0.pow(i).toLong())).duration(120)
                    .buildAndRegister()
            } else {
                assembler.builder()
                    .input(MetaTileEntities.CLAY_REACTOR)
                    .input(MetaTileEntities.CA_INJECTOR[i - 2])
                    .output(mte)
                    .tier(6)
                    .CEt(ClayEnergy.of(10.0.pow(i).toLong())).duration(120)
                    .buildAndRegister()
            }
        }
        /* Inscriber */
        for ((a, i) in MetaTileEntities.ASSEMBLER.take(2).zip(MetaTileEntities.INSCRIBER)) {
            assembler.builder()
                .input(a)
                .input(MetaItemClayParts.BASIC_CIRCUIT)
                .output(i)
                .tier(4).CEt(ClayEnergy.micro(100)).duration(40)
                .buildAndRegister()
        }
        registerLowTierRecipe(MetaTileEntities.INSCRIBER, "gMg", "cHc", "gcg")
    }

    /**
     * Register workbench recipes for tier <= 4 machines.
     * clay is [CMaterials.clay] if tier is 1, [CMaterials.denseClay] otherwise.
     * Some characters are available for recipes by default:
     * - 'H' for machine hull
     * - 'p' for plate and 'P' for large plate
     * - 'G' for grinding head and 'g' for gear
     * - 's' for short stick and 'S' for stick
     * - 'M' for spindle
     * - 'c' for circuit and 'C' for cutting head
     * - 'O' for pipe and 'I' for cylinder
     */
    private fun registerLowTierRecipe(metaTileEntities: List<MetaTileEntity>, vararg recipe: Any) {
        val circuits: List<Any> = listOf(
            Unit, // not used, but needed for indexing
            UnificationEntry(OrePrefix.gear, CMaterials.clay), MetaItemClayParts.CLAY_CIRCUIT, MetaItemClayParts.SIMPLE_CIRCUIT, MetaItemClayParts.BASIC_CIRCUIT,
            MetaItemClayParts.ADVANCED_CIRCUIT, MetaItemClayParts.PRECISION_CIRCUIT, MetaItemClayParts.INTEGRATED_CIRCUIT, MetaItemClayParts.CLAY_CORE,
            MetaItemClayParts.CLAY_BRAIN, MetaItemClayParts.CLAY_SPIRIT, MetaItemClayParts.CLAY_SOUL, MetaItemClayParts.CLAY_ANIMA, MetaItemClayParts.CLAY_PSYCHE,
        )
        for (mte in metaTileEntities) {
            if (mte.tier.numeric > 4 || mte.tier !is ClayTiers) continue
            val clay = if (mte.tier.numeric == 1) CMaterials.clay else CMaterials.denseClay
            val ingMap = mutableMapOf(
                'p' to UnificationEntry(OrePrefix.plate, clay), 'P' to UnificationEntry(OrePrefix.largePlate, clay),
                'g' to UnificationEntry(OrePrefix.gear, clay), 'G' to UnificationEntry(OrePrefix.cuttingHead, clay),
                's' to UnificationEntry(OrePrefix.stick, clay), 'S' to UnificationEntry(OrePrefix.stick, clay),
                'M' to UnificationEntry(OrePrefix.spindle, clay),
                'c' to circuits[mte.tier.numeric], 'C' to UnificationEntry(OrePrefix.cuttingHead, clay),
                'O' to UnificationEntry(OrePrefix.pipe, clay), 'I' to UnificationEntry(OrePrefix.cylinder, clay),
            )
            val recipeExtra = mutableListOf<Any>()
            for (row in recipe) {
                if (row !is String) continue
                for (c in row) {
                    val ing = ingMap[c]
                    if (ing != null) {
                        recipeExtra.add(c)
                        recipeExtra.add(ing)
                    }
                }
            }
            RecipeUtils.addShapedRecipe("${mte.metaTileEntityId}.workbench", mte.getStackForm(),
                *recipe,
                'H', MACHINE_HULL.getItem(mte.tier),
                *recipeExtra.toTypedArray()
            )
        }
    }

    private fun registerMachineRecipeHull(metaTileEntities: List<MetaTileEntity>, inputProvider: RecipeBuilder<*>.() -> RecipeBuilder<*>) {
        for (mte in metaTileEntities) {
            if (mte.tier !is ClayTiers) {
                Clayium.LOGGER.warn("MetaTileEntity ${mte.metaTileEntityId}'s tier is not a instance of ClayTiers. Cannot register recipe.")
                continue
            }
            CRecipes.ASSEMBLER.builder()
                .input(MACHINE_HULL.getItem(mte.tier))
                .output(mte.getStackForm())
                .tier(4).CEt(1.0).duration(60)
                .inputProvider()
                .buildAndRegister()
        }
    }

    private fun registerMachineRecipeBuffer(metaTileEntities: List<MetaTileEntity>, inputProvider: RecipeBuilder<*>.() -> RecipeBuilder<*>) {
        for (mte in metaTileEntities) {
            if (!(mte.tier is ClayTiers && mte.tier.numeric >= 4 && mte.tier.numeric <= 13)) continue
            val i = mte.tier.numeric - 4
            CRecipes.ASSEMBLER.builder()
                .input(MetaTileEntities.CLAY_BUFFER[i])
                .output(mte.getStackForm())
                .tier(4).CEt(1.0).duration(60)
                .inputProvider()
                .buildAndRegister()
        }
    }

    private fun clayBuffers() {
        val materials = listOf(CMaterials.advancedIndustrialClay, CMaterials.impureSilicon, CMaterials.aluminum,
            CMaterials.claySteel, CMaterials.clayium, CMaterials.ultimateAlloy, CMaterials.antimatter, CMaterials.pureAntimatter,
            CMaterials.octupleEnergyClay, CMaterials.octuplePureAntimatter)
        val circuits = listOf(MetaItemClayParts.BASIC_CIRCUIT, MetaItemClayParts.ADVANCED_CIRCUIT,
            MetaItemClayParts.PRECISION_CIRCUIT, MetaItemClayParts.INTEGRATED_CIRCUIT, MetaItemClayParts.CLAY_CORE,
            MetaItemClayParts.CLAY_BRAIN, MetaItemClayParts.CLAY_SPIRIT, MetaItemClayParts.CLAY_SOUL, MetaItemClayParts.CLAY_ANIMA, MetaItemClayParts.CLAY_PSYCHE)

        for (i in 0..9) {
            CRecipes.ASSEMBLER.builder()
                .input(OrePrefix.plate, materials[i])
                .input(circuits[i])
                .output(MetaTileEntities.CLAY_BUFFER[i], 16)
                .tier(4).CEt(ClayEnergy(10.0.pow((i + 1.0)).toLong())).duration(40)
                .buildAndRegister()
        }
    }

    private fun caCondenser() {
        val transformers = MetaTileEntities.MATTER_TRANSFORMER.filter { it.tier.numeric >= 9 }
        val hulls = ClayTiers.entries.filter { it.numeric >= 9 }.map { MACHINE_HULL.getItem(it) }
        for (tier in 9..11) {
            val i = tier - 9
            val hull = hulls[i]
            val transformer = transformers[i]
            CRecipes.ASSEMBLER.builder()
                .input(hull)
                .input(transformer, 16)
                .output(MetaTileEntities.CA_CONDENSER[i])
                .tier(if (tier == 9) 6 else 9 + i)
                .CEt(ClayEnergy.of(100 * 10.0.pow(i).toLong()))
                .duration(480)
                .buildAndRegister()
        }
    }
}