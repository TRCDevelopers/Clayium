package com.github.trcdeveloppers.clayium.common

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdeveloppers.clayium.common.blocks.machine.TileSingleSlotMachine
import com.github.trcdeveloppers.clayium.common.blocks.machine.claybuffer.TileClayBuffer
import com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable.TileClayWorkTable
import com.github.trcdeveloppers.clayium.common.interfaces.IShiftRightClickable
import com.github.trcdeveloppers.clayium.common.items.ClayiumItems
import com.github.trcdeveloppers.clayium.common.worldgen.ClayOreGenerator
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
        ClayiumItems.registerItems(event, Side.SERVER)
    }

    @SubscribeEvent
    open fun registerBlocks(event: RegistryEvent.Register<Block>) {
        ClayiumBlocks.registerBlocks(event, Side.SERVER)
    }

    open fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileClayWorkTable::class.java, ResourceLocation(Clayium.MOD_ID, "TileClayWorkTable"))
        GameRegistry.registerTileEntity(TileSingleSlotMachine::class.java, ResourceLocation(Clayium.MOD_ID, "TileSingleSlotMachine"))
        GameRegistry.registerTileEntity(TileClayBuffer::class.java, ResourceLocation(Clayium.MOD_ID, "TileClayBuffer"))
    }

    @SubscribeEvent
    fun onBlockRightClicked(e: PlayerInteractEvent.RightClickBlock) {
        val world = e.world
        val blockState = world.getBlockState(e.pos)
        val block = blockState.block

        if (block is IShiftRightClickable && e.entityPlayer.isSneaking) {
            if (block.onShiftRightClicked(world, e.pos, blockState, e.entityPlayer, e.hand, e.face ?: return, e.hitVec.x.toFloat(), e.hitVec.y.toFloat(), e.hitVec.z.toFloat())) {
                e.isCanceled = true
                e.entityPlayer.swingArm(e.hand)
            }
        }
    }
}
