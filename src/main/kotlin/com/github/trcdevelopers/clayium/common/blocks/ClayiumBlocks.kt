package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.Clayium.Companion.MOD_ID
import com.github.trcdevelopers.clayium.common.annotation.CBlock
import com.github.trcdevelopers.clayium.common.blocks.clay.BlockCompressedClay
import com.github.trcdevelopers.clayium.common.blocks.machine.claybuffer.BlockClayBuffer
import com.google.common.reflect.ClassPath
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import java.lang.reflect.Constructor
import java.util.Collections

object ClayiumBlocks {

    val COMPRESSED_CLAY = BlockCompressedClay()

    private val blocks: MutableMap<String, Block> = HashMap()
    @JvmStatic
    val allBlocks: Map<String, Block>
        get() = Collections.unmodifiableMap(blocks)

    fun getBlock(registryName: String): Block? {
        return blocks[registryName]
    }

    fun registerBlocks(event: RegistryEvent.Register<Block>, side: Side) {
        //参考 https://blog1.mammb.com/entry/2015/03/31/001620
        val classLoader = Thread.currentThread().contextClassLoader
        ClassPath.from(classLoader).getTopLevelClassesRecursive("com.github.trcdevelopers.clayium.common.blocks")
            .map(ClassPath.ClassInfo::load)
            .forEach { clazz ->
                val cBlock = clazz.getAnnotation(CBlock::class.java) ?: return@forEach

                if (cBlock.tiers.isEmpty()) {
                    val registryName = cBlock.registryName
                    registerBlock(clazz.newInstance() as Block, registryName)
                    return@forEach
                }

                val tieredConstructor = getTieredConstructor(clazz) ?: return@forEach
                if (cBlock.tiers.size == 1) {
                    val registryName = cBlock.registryName
                    registerBlock(tieredConstructor.newInstance(cBlock.tiers[0]) as Block, registryName)
                    return@forEach
                }

                cBlock.tiers.forEach { tier ->
                    val registryName = cBlock.registryName + "_tier$tier"
                    registerBlock(tieredConstructor.newInstance(tier) as Block, registryName)
                }
            }
        blocks.putAll(BlockClayBuffer.createBlocks())
        blocks.values.forEach(event.registry::register)
    }

    private fun registerBlock(block: Block, registryName: String) {
        block.creativeTab = Clayium.creativeTab
        block.registryName = ResourceLocation(MOD_ID, registryName)
        block.translationKey = "$MOD_ID.$registryName"
        blocks[registryName] = block
    }

    private fun getTieredConstructor(clazz: Class<*>): Constructor<*>? {
        return try {
            clazz.getConstructor(Int::class.java)
        } catch (e: NoSuchMethodException) {
            Clayium.LOGGER.warn("Class ${clazz.name} does not have a constructor with Int parameter. This will be ignored.")
            null
        }
    }
}
