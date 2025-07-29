package com.github.trc.clayium.client

import codechicken.lib.colour.ColourRGBA
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.gui.TextureExtra
import com.github.trc.clayium.client.model.MetaTileEntityModelLoader
import com.github.trc.clayium.client.model.MetalModelLoader
import com.github.trc.clayium.client.renderer.ClayLaserReflectorRenderer
import com.github.trc.clayium.client.renderer.ClayMarkerTESR
import com.github.trc.clayium.client.renderer.MetaTileEntityRenderDispatcher
import com.github.trc.clayium.client.renderer.MetalChestRenderer
import com.github.trc.clayium.common.CommonProxy
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.blocks.TileEntityClayLaserReflector
import com.github.trc.clayium.common.blocks.marker.TileClayMarker
import com.github.trc.clayium.common.blocks.metalchest.TileEntityMetalChest
import com.github.trc.clayium.common.items.metaitem.MetaItemClayium
import com.github.trc.clayium.common.metatileentities.MetaTileEntities
import com.github.trc.clayium.common.util.KeyInput
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry

private const val mode1velocity: Float = 0.7f
private const val mode2acceleration: Float = 0.9f
private const val mode2division: Float = 1.1f

@Suppress("unused")
@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy() {

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
        MinecraftForge.EVENT_BUS.register(KeyInput)

        ModelLoaderRegistry.registerLoader(MetaTileEntityModelLoader)
        ModelLoaderRegistry.registerLoader(MetalModelLoader)

        ClientRegistry.bindTileEntitySpecialRenderer(MetaTileEntityHolder::class.java, MetaTileEntityRenderDispatcher)
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityClayLaserReflector::class.java, ClayLaserReflectorRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(TileClayMarker.NoExtend::class.java, ClayMarkerTESR)
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMetalChest::class.java, MetalChestRenderer)

        ClayiumBlocks.CLAY_TREE_LEAVES.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
    }

    override fun registerItem(registry: IForgeRegistry<Item>, item: Item) {
        registry.register(item)
        if (item is MetaItemClayium) {
            item.registerModels()
        } else {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName!!, "inventory"))
        }
    }

    @SubscribeEvent
    fun onTextureStitchPre(event: TextureStitchEvent.Pre) {
        val compressedBlockTextures = listOf("metalblock_base", "metalblock_dark", "metalblock_light")
        for (block in ClayiumBlocks.COMPRESSED_BLOCKS) {
            for (material in block.mapping.values) {
                val colorsRaw = material.colors ?: return
                val name = material.upperCamelName

                val colors = colorsRaw.map { color ->
                    ColourRGBA(color shl 8).apply { a = 255.toByte() }
                }

                val sprite = TextureExtra(clayiumId("blocks/compressed_$name").toString(), compressedBlockTextures, colors)
                if (event.map.getTextureExtry(sprite.iconName) == null) {
                    event.map.setTextureEntry(sprite)
                }
            }
        }
    }

    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        ClayiumBlocks.registerStateMappers()
        ClayiumBlocks.registerModels()
        MetaTileEntities.registerItemModels()
    }

    @SubscribeEvent
    fun registerBlockColors(e: ColorHandlerEvent.Block) {
        ClayiumBlocks.registerBlockColors(e)
    }

    @SubscribeEvent
    fun registerItemColors(e: ColorHandlerEvent.Item) {
        ClayiumBlocks.registerItemColors(e)
        MetaItemClayium.registerColors(e)
    }

    override fun updateFlightStatus(mode: Int) {
        val mc = Minecraft.getMinecraft()
        val player = mc.player
            ?: return
        if (mode > 0 && player.capabilities.isFlying) {
            val mi = player.movementInput
            val settings = mc.gameSettings

            val sin = MathHelper.sin(player.rotationYaw * Math.PI.toFloat() / 180.0f)
            val cos = MathHelper.cos(player.rotationYaw * Math.PI.toFloat() / 180.0f)

            mi.moveForward = (player.motionZ * cos.toDouble() - player.motionX * sin.toDouble()).toFloat()
            mi.moveStrafe = (player.motionZ * sin.toDouble() + player.motionX * cos.toDouble()).toFloat()

            val disableInertia = mode >= 2

            if (mi.sneak) {
                if (disableInertia) {
                    player.motionY -= mode2acceleration.toDouble()
                    player.motionY /= mode2division.toDouble()
                } else {
                    player.motionY = (-mode1velocity).toDouble()
                }
            }

            if (mi.jump) {
                if (disableInertia) {
                    player.motionY += mode2acceleration.toDouble()
                    player.motionY /= mode2division.toDouble()
                } else {
                    player.motionY = mode1velocity.toDouble()
                }
            }

            if (mi.jump == mi.sneak) {
                player.motionY = 0.0
            }

            if (settings.keyBindForward.isPressed) {
                if (disableInertia) {
                    mi.moveForward += mode2acceleration
                    mi.moveForward /= mode2division
                } else {
                    mi.moveForward = mode1velocity
                }
            }

            if (settings.keyBindBack.isPressed) {
                if (disableInertia) {
                    mi.moveForward -= mode2acceleration
                    mi.moveForward /= mode2division
                } else {
                    mi.moveForward = -mode1velocity
                }
            }

            if (!settings.keyBindForward.isPressed && !settings.keyBindBack.isPressed) {
                mi.moveForward = 0.0f
            }

            if (settings.keyBindLeft.isPressed) {
                if (disableInertia) {
                    mi.moveStrafe += mode2acceleration
                    mi.moveStrafe /= mode2division
                } else {
                    mi.moveStrafe = mode1velocity
                }
            }

            if (settings.keyBindRight.isPressed) {
                if (disableInertia) {
                    mi.moveStrafe -= mode2acceleration
                    mi.moveStrafe /= mode2division
                } else {
                    mi.moveStrafe = -mode1velocity
                }
            }

            if (!settings.keyBindLeft.isPressed && !settings.keyBindRight.isPressed) {
                mi.moveStrafe = 0.0f
            }

            player.motionX = (mi.moveStrafe * cos - mi.moveForward * sin).toDouble()
            player.motionZ = (mi.moveForward * cos + mi.moveStrafe * sin).toDouble()
        }
    }
}
