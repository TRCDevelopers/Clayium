package com.github.trcdevelopers.clayium.common.worldgen

import com.github.trcdevelopers.clayium.common.blocks.ores.BlockClayOre
import net.minecraft.block.Block
import net.minecraft.block.BlockStone
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.gen.feature.WorldGenMinable
import net.minecraftforge.fml.common.IWorldGenerator
import net.minecraftforge.fml.common.registry.GameRegistry
import java.util.Random

/*
* all codes are from clayium and make these work same in 1.12.2
* but isn't support configuration for now
*/
class ClayOreGenerator : IWorldGenerator {
    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkGenerator, chunkProvider: IChunkProvider) {
        if (world.provider.dimension == 0) {
            generateOre(world, random, chunkX, chunkZ)
        }
    }

    private fun generateOre(world: World, random: Random, x: Int, z: Int) {
        var genZ: Int
        var genY: Int
        var genX: Int
        for (i in 0 until clayOreVeinNumber) {
            genX = x * 16 + random.nextInt(16)
            genY = clayOreVeinMinY + random.nextInt(clayOreVeinMaxY - clayOreVeinMinY)
            genZ = z * 16 + random.nextInt(16)
            WorldGenMinable( CLAY_ORE.defaultState, clayOreVeinSize ) { input: IBlockState? -> input?.block is BlockStone}
                .generate(world, random, BlockPos(genX, genY, genZ))
            if (!generateDenseClayOreVein) {
                continue
            }
            WorldGenMinable(DENSE_CLAY_ORE.defaultState, denseClayOreVeinSize) { input: IBlockState? -> input?.block is BlockClayOre }
                .generate(world, random, BlockPos(genX, genY, genZ))
            val pos = BlockPos(genX + 8, genY, genZ + 8)
            if (world.getBlockState(pos).block !== CLAY_ORE || world.getBlockState(pos).block !== DENSE_CLAY_ORE || random.nextInt(2) != 0) {
                continue
            }
            world.setBlockState(pos, LARGE_DENSE_CLAY_ORE.defaultState, 2)
        }
        for (i in 0 until largeDenseClayOreVeinNumber) {
            genX = x * 16 + random.nextInt(16)
            genY = largeDenseClayOreVeinMinY + random.nextInt(largeDenseClayOreVeinMaxY - largeDenseClayOreVeinMinY)
            genZ = z * 16 + random.nextInt(16)
            WorldGenMinable(LARGE_DENSE_CLAY_ORE.defaultState, largeDenseClayOreVeinSize) { input: IBlockState? -> input?.block is BlockStone }
                .generate(world, random, BlockPos(genX, genY, genZ))
        }
    }

    companion object {
        var clayOreVeinNumber = 8
        var clayOreVeinSize = 24
        var clayOreVeinMinY = 24
        var clayOreVeinMaxY = 88
        var generateDenseClayOreVein = true
        var denseClayOreVeinSize = 10
        var largeDenseClayOreVeinNumber = 2
        var largeDenseClayOreVeinSize = 6
        var largeDenseClayOreVeinMinY = 10
        var largeDenseClayOreVeinMaxY = 16

        @GameRegistry.ObjectHolder("clayium:clay_ore")
        lateinit var CLAY_ORE: Block

        @GameRegistry.ObjectHolder("clayium:dense_clay_ore")
        lateinit var DENSE_CLAY_ORE: Block

        @GameRegistry.ObjectHolder("clayium:large_dense_clay_ore")
        lateinit var LARGE_DENSE_CLAY_ORE: Block
    }
}
