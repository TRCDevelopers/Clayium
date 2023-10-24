package com.github.trcdeveloppers.clayium.common.blocks

import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import com.github.trcdeveloppers.clayium.common.annotation.CBlock
import com.github.trcdeveloppers.clayium.common.creativetab.ClayiumCreativeTab
import com.google.common.reflect.ClassPath
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import java.util.*

object ClayiumBlocks {
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
        ClassPath.from(classLoader).getTopLevelClassesRecursive("com.github.trcdeveloppers.clayium.common.blocks")
            .map(ClassPath.ClassInfo::load)
            .forEach {clazz ->
                val cBlock = clazz.getAnnotation(CBlock::class.java) ?: return@forEach
                val block = clazz.newInstance() as Block
                val registryName = cBlock.registryName;

                block.creativeTab = ClayiumCreativeTab.CLAYIUM
                block.registryName = ResourceLocation(MOD_ID, registryName)
                block.translationKey = "$MOD_ID.$registryName"
                blocks[registryName] = block
            }
        blocks.putAll(CompressedClay.createBlocks())
        blocks.values.forEach(event.registry::register)
    }
}
