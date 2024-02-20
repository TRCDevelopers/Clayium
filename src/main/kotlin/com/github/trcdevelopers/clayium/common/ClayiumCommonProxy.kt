package com.github.trcdevelopers.clayium.common

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.TileClayWorkTable
import com.github.trcdevelopers.clayium.common.interfaces.IShiftRightClickable
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaPrefixItem
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.worldgen.ClayOreGenerator
import net.minecraft.block.Block
import net.minecraft.item.Item
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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.IForgeRegistry

open class ClayiumCommonProxy {
    open fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(Clayium.proxy)
        this.registerTileEntities()
        GameRegistry.registerWorldGenerator(ClayOreGenerator(), 0)
        NetworkRegistry.INSTANCE.registerGuiHandler(Clayium.INSTANCE, GuiHandler)
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun postInit(event: FMLPostInitializationEvent) {
    }

    @SubscribeEvent
    open fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry = event.registry

        for (orePrefix in OrePrefix.entries) {
            val metaPrefixItem = MetaPrefixItem.create("meta_${orePrefix.snake}", orePrefix)
            registry.register(metaPrefixItem)
            metaPrefixItem.registerSubItems()
        }

        ClayiumItems.registerItems(event, Side.SERVER)
    }

    @SubscribeEvent
    open fun registerBlocks(event: RegistryEvent.Register<Block>) {
        ClayiumBlocks.registerBlocks(event, Side.SERVER)
    }

    open fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileClayWorkTable::class.java, ResourceLocation(Clayium.MOD_ID, "TileClayWorkTable"))
        GameRegistry.registerTileEntity(TileClayBuffer::class.java, ResourceLocation(Clayium.MOD_ID, "TileClayBuffer"))
    }

    open fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        registry.register(item)
    }

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
