package com.github.trc.clayium.common.blocks

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.W
import com.github.trc.clayium.api.block.VariantItemBlock
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.ClayiumMod
import com.github.trc.clayium.common.blocks.chunkloader.ChunkLoaderBlock
import com.github.trc.clayium.common.blocks.claycraftingtable.BlockClayCraftingBoard
import com.github.trc.clayium.common.blocks.claytree.BlockClayLeaves
import com.github.trc.clayium.common.blocks.claytree.BlockClayLog
import com.github.trc.clayium.common.blocks.claytree.BlockClaySapling
import com.github.trc.clayium.common.blocks.clayworktable.BlockClayWorkTable
import com.github.trc.clayium.common.blocks.marker.BlockClayMarker
import com.github.trc.clayium.common.blocks.material.BlockCompressed
import com.github.trc.clayium.common.blocks.material.BlockCompressedClay
import com.github.trc.clayium.common.blocks.material.BlockEnergizedClay
import com.github.trc.clayium.common.blocks.ores.BlockClayOre
import com.github.trc.clayium.common.blocks.ores.BlockDenseClayOre
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import net.minecraft.block.Block
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockSapling
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ClayiumBlocks {

    private val blocks: MutableMap<String, Block> = mutableMapOf()

    val CREATIVE_ENERGY_SOURCE = createBlock("creative_energy_source", BlockSimpleTileEntityHolder(::TileEntityCreativeEnergySource)
        .apply { setBlockUnbreakable() })

    val CLAY_CRAFTING_BOARD = createBlock("clay_crafting_board", BlockClayCraftingBoard())
    val CLAY_WORK_TABLE = createBlock("clay_work_table", BlockClayWorkTable())

    val CLAY_ORE = createBlock("clay_ore", BlockClayOre())
    val DENSE_CLAY_ORE = createBlock("dense_clay_ore", BlockDenseClayOre())
    val LARGE_DENSE_CLAY_ORE = createBlock("large_dense_clay_ore", BlockDenseClayOre())

    val LASER_REFLECTOR = createBlock("laser_reflector", BlockClayLaserReflector())
    val QUARTZ_CRUCIBLE = createBlock("quartz_crucible", BlockQuartzCrucible())
    val MACHINE_HULL = createBlock("machine_hull", BlockMachineHull())
    val RESONATOR = createBlock("resonator", BlockResonator())
    val CA_REACTOR_HULL = createBlock("ca_reactor_hull", BlockCaReactorHull())
    val CA_REACTOR_COIL = createBlock("ca_reactor_coil", BlockCaReactorCoil())

    val PAN_CABLE = createBlock("pan_cable", BlockPanCable())

    val CLAY_TREE_LOG = createBlock("clay_tree_log", BlockClayLog())
    val CLAY_TREE_LEAVES = createBlock("clay_tree_leaves", BlockClayLeaves())
    val CLAY_TREE_SAPLING = createBlock("clay_tree_sapling", BlockClaySapling())

    val OVERCLOCKER = createBlock("overclocker", BlockOverclocker())
    val ENERGY_STORAGE_UPGRADE = createBlock("energy_storage_upgrade", BlockEnergyStorageUpgrade())

    val CLAY_MARKER = createBlock("clay_marker", BlockClayMarker())

    val CHUNK_LOADER = createBlock("chunk_loader", ChunkLoaderBlock())

    /* Deco Blocks */
    val COLORED_SILICONE = createBlock("colored_silicone", ColoredSiliconeBlock(), ClayiumCTabs.decorations)

    /* ---------------------------------- */

    val COMPRESSED_CLAY_BLOCKS = mutableListOf<BlockCompressedClay>()
    val ENERGIZED_CLAY_BLOCKS = mutableListOf<BlockEnergizedClay>()
    val COMPRESSED_BLOCKS = mutableListOf<BlockCompressed>()

    private val compressedClay = mutableMapOf<CMaterial, BlockCompressedClay>()
    private val energizedClay = mutableMapOf<CMaterial, BlockEnergizedClay>()
    private val compressedBlocks = mutableMapOf<CMaterial, BlockCompressed>()

    /* ---------------------------------- */

    private val stateMapperCache = mutableMapOf<Block, IStateMapper>()
    private val COMPRESSED_ITEM_BLOCKS = mutableListOf<ItemBlockMaterial>()

    init {
        createMaterialBlock(
            { !OrePrefix.block.isIgnored(it)
                && it.hasProperty(CPropertyKey.CLAY) && it.getProperty(CPropertyKey.CLAY).energy == null },
            this::createCompressedClayBlock)
        createMaterialBlock(
            { !OrePrefix.block.isIgnored(it)
                && it.getPropOrNull(CPropertyKey.CLAY)?.energy != null },
            this::createEnergizedClayBlock)
        createMaterialBlock(
            { !OrePrefix.block.isIgnored(it)
                && (it.hasProperty(CPropertyKey.INGOT) || it.hasProperty(CPropertyKey.MATTER)) },
            this::createCompressedBock)
    }

    private fun <T: Block> createBlock(key: String, block: T, tab: CreativeTabs = ClayiumMod.creativeTab): T {
        return block.apply {
            setCreativeTab(tab)
            setRegistryName(clayiumId(key))
            setTranslationKey("${MOD_ID}.$key")
            blocks[key] = this
        }
    }

    private fun <T: Block, I: ItemBlock> createItemBlock(block: T, producer: (T) -> I): I {
        return producer(block).apply {
            registryName = block.registryName ?: throw IllegalArgumentException("Block ${block.translationKey} has no registry name")
        }
    }

    fun registerBlocks(event: RegistryEvent.Register<Block>) { blocks.values.forEach(event.registry::register) }

    fun registerItemBlocks(event: RegistryEvent.Register<Item>) {
        val registry = event.registry
        for (block in COMPRESSED_BLOCKS) {
            val ib = createItemBlock(block) { ItemBlockMaterial(it, OrePrefix.block) }
            registry.register(ib)
            COMPRESSED_ITEM_BLOCKS.add(ib)
        }

        registry.register(createItemBlock(COLORED_SILICONE, ::VariantItemBlock))
    }

    fun registerOreDictionaries() {
        OreDictUnifier.registerOre(ItemStack(COLORED_SILICONE, 1, W), OrePrefix.block, CMaterials.silicone)
        for ((m, b) in energizedClay) {
            val stack = b.getItemStack(m)
            OreDictUnifier.registerOre(stack, OrePrefix.block, m)
        }
        for ((m, b) in compressedClay) {
            val stack = b.getItemStack(m)
            OreDictUnifier.registerOre(stack, OrePrefix.block, m)
        }
        for ((m, b) in compressedBlocks) {
            val stack = b.getItemStack(m)
            OreDictUnifier.registerOre(stack, OrePrefix.block, m)
        }
    }

    fun createMaterialBlock(filter: (material: CMaterial) -> Boolean, generator: (metaMaterialMap: Map<Int, CMaterial>, index: Int) -> Unit) {
        var mapping = Int2ObjectArrayMap<CMaterial>(17)
        for ((currentId, materials) in ClayiumApi.materialRegistry.chunked(16).withIndex()) {
            for (material in materials) {
                if (!filter(material)) continue
                val metaItemSubId = material.metaItemSubId % 16
                mapping.put(metaItemSubId, material)
            }
            if (mapping.isNotEmpty()) {
                generator(mapping, currentId)
                mapping = Int2ObjectArrayMap(17)
            }
        }
    }

    fun createEnergizedClayBlock(metaMaterialMap: Map<Int, CMaterial>, index: Int) {
        val block = BlockEnergizedClay.Companion.create(metaMaterialMap)
        block.registryName = clayiumId("energized_clay_$index")
        ENERGIZED_CLAY_BLOCKS.add(block)
        metaMaterialMap.values.forEach { energizedClay[it] = block }
    }

    fun createCompressedClayBlock(metaMaterialMap: Map<Int, CMaterial>, index: Int) {
        val block = BlockCompressedClay.create(metaMaterialMap)
        block.registryName = clayiumId("compressed_clay_$index")
        COMPRESSED_CLAY_BLOCKS.add(block)
        metaMaterialMap.values.forEach { compressedClay[it] = block }
    }

    fun createCompressedBock(metaMaterialMap: Map<Int, CMaterial>, index: Int) {
        val block = BlockCompressed.create(metaMaterialMap)
        block.registryName = clayiumId("compressed_block_$index")
        COMPRESSED_BLOCKS.add(block)
        metaMaterialMap.values.forEach { compressedBlocks[it] = block }
    }

    @SideOnly(Side.CLIENT)
    fun registerStateMappers() {
        setStateMapper(CLAY_TREE_LEAVES, StateMap.Builder().ignore(BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build())
        setStateMapper(CLAY_TREE_SAPLING, StateMap.Builder().ignore(BlockSapling.STAGE).build())
        setStateMapper(COLORED_SILICONE, StateMap.Builder().ignore(COLORED_SILICONE.variantProperty).build())
    }

    @SideOnly(Side.CLIENT)
    private fun setStateMapper(block: Block, stateMapper: IStateMapper) {
        stateMapperCache[block] = stateMapper
        ModelLoader.setCustomStateMapper(block, stateMapper)
    }

    @SideOnly(Side.CLIENT)
    fun registerModels() {
        blocks.values.forEach(::registerItemModel)
        for (block in ENERGIZED_CLAY_BLOCKS) block.registerModels()
        for (block in COMPRESSED_CLAY_BLOCKS) block.registerModels()
        for (block in COMPRESSED_BLOCKS) block.registerModels()

        stateMapperCache.clear()
    }

    @SideOnly(Side.CLIENT)
    private fun registerItemModel(block: Block) {
        val item = block.getAsItem()
        val defaultStateMapper = DefaultStateMapper()
        when (block) {
            CLAY_TREE_SAPLING -> ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(clayiumId("clay_tree_sapling"), "inventory"))
            PAN_CABLE -> ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(clayiumId("pan_cable"), "inventory"))
            else -> {
                val customStateMapper = stateMapperCache[block]
                if (customStateMapper != null) {
                    val map = customStateMapper.putStateModelLocations(block)
                    for (state in block.blockState.validStates) {
                        ModelLoader.setCustomModelResourceLocation(item, block.getMetaFromState(state),
                            map[state] ?: error("Missing model for state $state"))
                    }
                } else {
                    for (state in block.blockState.validStates) {
                        ModelLoader.setCustomModelResourceLocation(item, block.getMetaFromState(state),
                            ModelResourceLocation(block.registryName!!, defaultStateMapper.getPropertyString(state.properties)))
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    fun registerBlockColors(e: ColorHandlerEvent.Block) {
        val blockColors = e.blockColors
        for (block in COMPRESSED_BLOCKS) {
            blockColors.registerBlockColorHandler({ state, _, _, i ->
                block.getCMaterial(state).colors?.get(i) ?: 0
            }, block)
        }
        blockColors.registerBlockColorHandler({ state, _, _, _ ->
            COLORED_SILICONE.getEnum(state).colorValue
        }, COLORED_SILICONE)
    }

    @SideOnly(Side.CLIENT)
    fun registerItemColors(e: ColorHandlerEvent.Item) {
        val itemColors = e.itemColors
        for (item in COMPRESSED_ITEM_BLOCKS) {
            itemColors.registerItemColorHandler({ stack, i ->
                item.blockMaterial.getCMaterial(stack.itemDamage).colors?.get(i) ?: 0
            }, item)
        }
        itemColors.registerItemColorHandler({ stack, _ ->
            COLORED_SILICONE.getEnum(stack).colorValue
        }, COLORED_SILICONE)
    }
}
