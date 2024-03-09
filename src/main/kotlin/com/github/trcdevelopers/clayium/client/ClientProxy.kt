package com.github.trcdevelopers.clayium.client

import com.github.trcdevelopers.clayium.client.loader.ClayBufferModelLoader
import com.github.trcdevelopers.clayium.client.tesr.ClayBufferPipeIoRenderer
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.CommonProxy
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.clay.ItemBlockEnergizedClay
import com.github.trcdevelopers.clayium.common.blocks.machine.BlockMachine
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaPrefixItem
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry

@Suppress("unused")
@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy() {

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
        ModelLoaderRegistry.registerLoader(ClayBufferModelLoader)
        ClientRegistry.bindTileEntitySpecialRenderer(TileClayBuffer::class.java, ClayBufferPipeIoRenderer)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
        ClayiumItems.registerItemColors()
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
        for (item in MetaItemClayium.META_ITEMS) {
            item.registerColorHandler()
        }
    }

    @SubscribeEvent
    override fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry = event.registry

        for (orePrefix in OrePrefix.entries) {
            val metaPrefixItem = MetaPrefixItem.create("meta_${orePrefix.snake}", orePrefix)
            registry.register(metaPrefixItem)
            metaPrefixItem.registerSubItems()
            metaPrefixItem.registerModels()
        }
        ClayiumItems.registerItems(event, Side.CLIENT)

        // todo: move model registration to ClayiumBlocks
        registry.register(ItemBlockCompressedClay(ClayiumBlocks.COMPRESSED_CLAY).apply { registerModels() })
        registry.register(ItemBlockEnergizedClay(ClayiumBlocks.ENERGIZED_CLAY).apply { registerModels() })

        registry.register(createItemBlock(ClayiumBlocks.CLAY_WORK_TABLE, ::ItemBlock))
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

    override fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        if (item is MetaItemClayium) {
            item.registerModels()
        }
        super.registerItem(registry, item)
    }

    override fun registerMachineItemBlock(registry: IForgeRegistry<Item>, machineName: String, tier: Int, block: BlockMachine): Item {
        return super.registerMachineItemBlock(registry, machineName, tier, block).also {
            ModelLoader.setCustomModelResourceLocation(it, 0, ModelResourceLocation("${Clayium.MOD_ID}:$machineName", "tier=$tier"))
        }
    }

    override fun <T : Block> createItemBlock(block: T, producer: (T) -> ItemBlock): ItemBlock {
        return super.createItemBlock(block, producer).apply {
            ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(block.registryName!!, "inventory"))
        }
    }
}
