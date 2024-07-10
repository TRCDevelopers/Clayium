package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.util.clayiumId
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.BlockClayWorkTable
import com.github.trcdevelopers.clayium.common.blocks.material.BlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockClayOre
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockDenseClayOre
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import com.google.common.collect.ImmutableMap
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ClayiumBlocks {

    private val blocks: MutableMap<String, Block> = mutableMapOf()
    val allBlocks: Map<String, Block> get() = ImmutableMap.copyOf(blocks)

    val CREATIVE_ENERGY_SOURCE = createBlock("creative_energy_source", BlockSimpleTileEntityHolder(::TileEntityCreativeEnergySource))

    val CLAY_WORK_TABLE = createBlock("clay_work_table", BlockClayWorkTable())

    val CLAY_ORE = createBlock("clay_ore", BlockClayOre())
    val DENSE_CLAY_ORE = createBlock("dense_clay_ore", BlockDenseClayOre())
    val LARGE_DENSE_CLAY_ORE = createBlock("large_dense_clay_ore", BlockDenseClayOre())

    val LASER_REFLECTOR = createBlock("laser_reflector", BlockClayLaserReflector())
    val QUARTZ_CRUCIBLE = createBlock("quartz_crucible", BlockQuartzCrucible())
    val MACHINE_HULL = createBlock("machine_hull", BlockMachineHull())
    val RESONATOR = createBlock("resonator", BlockResonator())

    /* ---------------------------------- */

    val COMPRESSED_CLAY_BLOCKS = mutableListOf<BlockCompressedClay>()
    val ENERGIZED_CLAY_BLOCKS = mutableListOf<BlockEnergizedClay>()

    private val compressedClay = mutableMapOf<Material, BlockCompressedClay>()
    private val energizedClay = mutableMapOf<Material, BlockEnergizedClay>()

    init {
        createMaterialBlock(
            { !OrePrefix.block.isIgnored(it)
                && it.hasProperty(PropertyKey.CLAY) && it.getProperty(PropertyKey.CLAY).energy == null },
            this::createCompressedClayBlock)
        createMaterialBlock(
            { !OrePrefix.block.isIgnored(it)
                && it.getPropOrNull(PropertyKey.CLAY)?.energy != null },
            this::createEnergizedClayBlock)
    }

    private fun <T: Block> createBlock(key: String, block: T): T {
        return block.apply {
            setCreativeTab(Clayium.creativeTab)
            setRegistryName(clayiumId(key))
            setTranslationKey("${CValues.MOD_ID}.$key")
            blocks[key] = this
        }
    }

    fun registerBlocks(event: RegistryEvent.Register<Block>) { blocks.values.forEach(event.registry::register) }

    fun registerOreDictionaries() {
        for ((m, b) in energizedClay) {
            val stack = b.getItemStack(m)
            OreDictUnifier.registerOre(stack, OrePrefix.block, m)
        }

        for ((m, b) in compressedClay) {
            val stack = b.getItemStack(m)
            OreDictUnifier.registerOre(stack, OrePrefix.block, m)
        }
    }

    fun createMaterialBlock(filter: (material: Material) -> Boolean, generator: (metaMaterialMap: Map<Int, Material>, index: Int) -> Unit) {
        var currentId = 0
        var mapping = Int2ObjectArrayMap<Material>(17)
        for (materials in ClayiumApi.materialRegistry.chunked(16)) {
            for (material in materials) {
                if (!filter(material)) continue
                val metaItemSubId = material.metaItemSubId % 16
                mapping.put(metaItemSubId, material)
            }
            if (mapping.isNotEmpty()) {
                generator(mapping, currentId)
                mapping = Int2ObjectArrayMap(17)
            }
            currentId++
        }
    }

    fun createEnergizedClayBlock(metaMaterialMap: Map<Int, Material>, index: Int) {
        val block = BlockEnergizedClay.create(metaMaterialMap)
        block.registryName = clayiumId("energized_clay_$index")
        ENERGIZED_CLAY_BLOCKS.add(block)
        metaMaterialMap.values.forEach { energizedClay[it] = block }
    }

    fun createCompressedClayBlock(metaMaterialMap: Map<Int, Material>, index: Int) {
        val block = BlockCompressedClay.create(metaMaterialMap)
        block.registryName = clayiumId("compressed_clay_$index")
        COMPRESSED_CLAY_BLOCKS.add(block)
        metaMaterialMap.values.forEach { compressedClay[it] = block }
    }

    //todo
    fun getCompressedClayStack(tier: Int): ItemStack {
        return ItemStack(Blocks.CLAY, 1)
    }

    @SideOnly(Side.CLIENT)
    fun registerModels() {
        blocks.values.forEach(this::registerItemModel)
        for (block in ENERGIZED_CLAY_BLOCKS) block.registerModels()
        for (block in COMPRESSED_CLAY_BLOCKS) block.registerModels()
    }

    @SideOnly(Side.CLIENT)
    private fun registerItemModel(block: Block) {
        for (state in block.blockState.validStates) {
            if (block.blockState.properties.isEmpty()) {
                ModelLoader.setCustomModelResourceLocation(
                    Item.getItemFromBlock(block), 0,
                    ModelResourceLocation(block.registryName!!, "normal")
                )
            } else {
                val meta = block.getMetaFromState(state)
                ModelLoader.setCustomModelResourceLocation(
                    Item.getItemFromBlock(block), meta,
                    ModelResourceLocation(block.registryName!!, "meta=$meta")
                )
            }
        }
    }
}
