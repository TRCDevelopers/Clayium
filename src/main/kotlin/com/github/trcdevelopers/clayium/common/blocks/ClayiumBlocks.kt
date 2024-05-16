package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.BlockClayWorkTable
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockClayOre
import com.github.trcdevelopers.clayium.common.blocks.ores.BlockDenseClayOre
import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ClayiumBlocks {

    val CLAY_WORK_TABLE = createBlock("clay_work_table", BlockClayWorkTable())

    val COMPRESSED_CLAY = createBlock("compressed_clay", BlockCompressedClay())
    val ENERGIZED_CLAY = createBlock("energized_clay", BlockEnergizedClay())

    val CLAY_ORE = createBlock("clay_ore", BlockClayOre())
    val DENSE_CLAY_ORE = createBlock("dense_clay_ore", BlockDenseClayOre())
    val LARGE_DENSE_CLAY_ORE = createBlock("large_dense_clay_ore", BlockDenseClayOre())

    val LASER_REFLECTOR = createBlock("laser_reflector", BlockClayLaserReflector())

    private val blocks: MutableMap<String, Block> = HashMap()
    val allBlocks: Map<String, Block> get() = ImmutableMap.copyOf(blocks)

    private fun <T: Block> createBlock(key: String, block: T): T {
        return block.apply {
            setCreativeTab(Clayium.creativeTab)
            setRegistryName(clayiumId(key))
            setTranslationKey("${CValues.MOD_ID}.$key")
        }
    }

    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        val registry = event.registry
        registry.register(CLAY_WORK_TABLE)

        registry.register(COMPRESSED_CLAY)
        registry.register(ENERGIZED_CLAY)

        registry.register(CLAY_ORE)
        registry.register(DENSE_CLAY_ORE)
        registry.register(LARGE_DENSE_CLAY_ORE)

        registry.register(LASER_REFLECTOR)
    }

    @SideOnly(Side.CLIENT)
    fun registerItemBlockModels() {
        registerItemModel(CLAY_WORK_TABLE)

        registerItemModel(COMPRESSED_CLAY)
        registerItemModel(ENERGIZED_CLAY)

        registerItemModel(CLAY_ORE)
        registerItemModel(DENSE_CLAY_ORE)
        registerItemModel(LARGE_DENSE_CLAY_ORE)

        registerItemModel(LASER_REFLECTOR)
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
