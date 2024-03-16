package com.github.trcdevelopers.clayium.common

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.TileClayWorkTable
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileEntityClayBuffer
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.tile.TileSimpleMachine
import com.github.trcdevelopers.clayium.common.interfaces.IShiftRightClickable
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.items.ItemBlockTiered
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaPrefixItem
import com.github.trcdevelopers.clayium.common.recipe.loader.CRecipeLoader
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.worldgen.ClayOreGenerator
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(Clayium.proxy)
        this.registerTileEntities()
        GameRegistry.registerWorldGenerator(ClayOreGenerator(), 0)
        NetworkRegistry.INSTANCE.registerGuiHandler(Clayium.INSTANCE, GuiHandler)
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun postInit(event: FMLPostInitializationEvent) {
        CRecipeLoader.load()
    }

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        val registry: IForgeRegistry<Block> = event.registry

        registerBlock(registry, ClayiumBlocks.CLAY_WORK_TABLE)
        for (buffer in MachineBlocks.CLAY_BUFFER.values) {
            registerBlock(registry, buffer)
        }

        registerBlock(registry, ClayiumBlocks.COMPRESSED_CLAY)
        registerBlock(registry, ClayiumBlocks.ENERGIZED_CLAY)

        registerBlock(registry, ClayiumBlocks.CLAY_ORE)
        registerBlock(registry, ClayiumBlocks.DENSE_CLAY_ORE)
        registerBlock(registry, ClayiumBlocks.LARGE_DENSE_CLAY_ORE)

        for (machines in MachineBlocks.ALL_MACHINES) {
            for (machine in machines.value) {
                registerBlock(registry, machine.value)
            }
        }
    }

    open fun registerBlock(registry: IForgeRegistry<Block>, block: Block) {
        registry.register(block)
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry = event.registry

        registry.register(MetaItemClayParts)
        for (orePrefix in OrePrefix.entries) {
            val metaPrefixItem = MetaPrefixItem.create("meta_${orePrefix.snake}", orePrefix)
            registry.register(metaPrefixItem)
            metaPrefixItem.registerSubItems()
        }

        registerItem(registry, ClayiumItems.CLAY_ROLLING_PIN)
        registerItem(registry, ClayiumItems.CLAY_SLICER)
        registerItem(registry, ClayiumItems.CLAY_SPATULA)
        registerItem(registry, ClayiumItems.CLAY_WRENCH)
        registerItem(registry, ClayiumItems.CLAY_IO_CONFIGURATOR)
        registerItem(registry, ClayiumItems.CLAY_PIPING_TOOL)

        registerItem(registry, ClayiumItems.CLAY_PICKAXE)
        registerItem(registry, ClayiumItems.CLAY_SHOVEL)

        registry.register(createItemBlock(ClayiumBlocks.CLAY_WORK_TABLE, ItemBlockTiered::create))

        registry.register(createItemBlock(ClayiumBlocks.COMPRESSED_CLAY, ::ItemBlockCompressedClay))
        registry.register(createItemBlock(ClayiumBlocks.ENERGIZED_CLAY, ::ItemBlockEnergizedClay))

        registry.register(createItemBlock(ClayiumBlocks.CLAY_ORE, ::ItemBlock))
        registry.register(createItemBlock(ClayiumBlocks.DENSE_CLAY_ORE, ::ItemBlock))
        registry.register(createItemBlock(ClayiumBlocks.LARGE_DENSE_CLAY_ORE, ::ItemBlock))

        for (machines in MachineBlocks.ALL_MACHINES) {
            val machineName = machines.key
            for (machine in machines.value) {
                registerMachineItemBlock(registry, machineName, machine.key, machine.value)
            }
        }
    }

    open fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        registry.register(item)
    }

    private fun <T: Block> createItemBlock(block: T, producer: (T) -> ItemBlock): ItemBlock {
        return producer(block).apply {
            registryName = block.registryName ?: throw IllegalArgumentException("Block ${block.translationKey} has no registry name")
        }
    }

    open fun registerMachineItemBlock(registry: IForgeRegistry<Item>, machineName: String, tier: Int, block: BlockMachine): Item {
        val itemBlock = ItemBlock(block).setRegistryName(block.registryName)
        registry.register(itemBlock)
        return itemBlock
    }

    open fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileClayWorkTable::class.java, ResourceLocation(Clayium.MOD_ID, "tile_clay_work_table"))

        GameRegistry.registerTileEntity(TileMachine::class.java, ResourceLocation(Clayium.MOD_ID, "tile_machine"))
        GameRegistry.registerTileEntity(TileSimpleMachine::class.java, ResourceLocation(Clayium.MOD_ID, "tile_simple_machine"))
        GameRegistry.registerTileEntity(TileEntityClayBuffer::class.java, ResourceLocation(Clayium.MOD_ID, "tile_clay_buffer"))
    }

    // todo: move this to item
    @SubscribeEvent
    fun onBlockRightClicked(e: PlayerInteractEvent.RightClickBlock) {
        val world = e.world
        val blockState = world.getBlockState(e.pos)
        val block = blockState.block

        if (block is IShiftRightClickable && e.entityPlayer.isSneaking) {
            val (cancel, swing) = block.onShiftRightClicked(world, e.pos, blockState, e.entityPlayer, e.hand, e.face ?: return, e.hitVec.x.toFloat(), e.hitVec.y.toFloat(), e.hitVec.z.toFloat())
            e.isCanceled = cancel
            if (swing) e.entityPlayer.swingArm(e.hand)
        }
    }
}
