package com.github.trc.clayium.common.worldgen

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.common.config.ConfigCore.worldGen
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.gen.feature.WorldGenMinable
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.IWorldGenerator
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import java.util.Random

object ClayOreGenerator : IWorldGenerator {

    @GameRegistry.ObjectHolder("clayium:clay_ore")
    lateinit var CLAY_ORE: Block

    @GameRegistry.ObjectHolder("clayium:dense_clay_ore")
    lateinit var DENSE_CLAY_ORE: Block

    @GameRegistry.ObjectHolder("clayium:large_dense_clay_ore")
    lateinit var LARGE_DENSE_CLAY_ORE: Block

    private var clayOreGenerator = WorldGenMinable(CLAY_ORE.defaultState, worldGen.clayOreVeinSize)
    private var denseClayOreGenerator = WorldGenMinable(DENSE_CLAY_ORE.defaultState, worldGen.denseClayOreVeinSize)
    private var largeDenseClayOreGenerator = WorldGenMinable(LARGE_DENSE_CLAY_ORE.defaultState, worldGen.largeDenseClayOreVeinSize)

    @Suppress("unused")
    @SubscribeEvent
    fun onConfigChanged(e: ConfigChangedEvent.OnConfigChangedEvent) {
        if (e.modID != CValues.MOD_ID) return

        clayOreGenerator = WorldGenMinable(CLAY_ORE.defaultState, worldGen.clayOreVeinSize)
        denseClayOreGenerator = WorldGenMinable(DENSE_CLAY_ORE.defaultState, worldGen.denseClayOreVeinSize)
        largeDenseClayOreGenerator = WorldGenMinable(LARGE_DENSE_CLAY_ORE.defaultState, worldGen.largeDenseClayOreVeinSize)
    }

    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkGenerator, chunkProvider: IChunkProvider) {
        if (world.provider.dimension == 0) {
            generateOre(world, random, chunkX, chunkZ)
        }
    }

    @Suppress("DuplicatedCode")
    private fun generateOre(world: World, random: Random, x: Int, z: Int) {
        @Suppress("unused")
        for (i in 0..<worldGen.clayOreVeinNumber) {
            val genX = x * 16 + random.nextInt(16)
            val genY = worldGen.clayOreVeinMinY + random.nextInt(worldGen.clayOreVeinMaxY - worldGen.clayOreVeinMinY)
            val genZ = z * 16 + random.nextInt(16)
            clayOreGenerator.generate(world, random, BlockPos(genX, genY, genZ))
            if (worldGen.generateDenseClayOreVein) {
                denseClayOreGenerator.generate(world, random, BlockPos(genX, genY, genZ))
            }
            val pos = BlockPos(genX + 8, genY, genZ + 8)
            if (world.getBlockState(pos).block !== CLAY_ORE || world.getBlockState(pos).block !== DENSE_CLAY_ORE || random.nextInt(2) != 0) {
                continue
            }
            world.setBlockState(pos, LARGE_DENSE_CLAY_ORE.defaultState, 2)
        }
        @Suppress("unused")
        for (i in 0..<worldGen.largeDenseClayOreVeinNumber) {
            val genX = x * 16 + random.nextInt(16)
            val genY = worldGen.largeDenseClayOreVeinMinY + random.nextInt(worldGen.largeDenseClayOreVeinMaxY - worldGen.largeDenseClayOreVeinMinY)
            val genZ = z * 16 + random.nextInt(16)
            largeDenseClayOreGenerator.generate(world, random, BlockPos(genX, genY, genZ))
        }
    }
}
