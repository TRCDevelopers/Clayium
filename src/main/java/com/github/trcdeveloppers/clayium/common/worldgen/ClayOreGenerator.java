package com.github.trcdeveloppers.clayium.common.worldgen;

import com.github.trcdeveloppers.clayium.common.blocks.ores.BlockClayOre;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

/*
 * all codes are from clayium and make these work same in 1.12.2
 * but isn't support configuration for now
 */

public class ClayOreGenerator implements IWorldGenerator {
    static int clayOreVeinNumber = 8;
    static int clayOreVeinSize = 24;
    static int clayOreVeinMinY = 24;
    static int clayOreVeinMaxY = 88;
    static boolean generateDenseClayOreVein = true;
    static int denseClayOreVeinSize = 10;
    static int largeDenseClayOreVeinNumber = 2;
    static int largeDenseClayOreVeinSize = 6;
    static int largeDenseClayOreVeinMinY = 10;
    static int largeDenseClayOreVeinMaxY = 16;

    @GameRegistry.ObjectHolder("clayium:clay_ore")
    public static final Block CLAY_ORE = null;

    @GameRegistry.ObjectHolder("clayium:dense_clay_ore")
    public static final Block DENSE_CLAY_ORE = null;

    @GameRegistry.ObjectHolder("clayium:large_dense_clay_ore")
    public static final Block LARGE_DENSE_CLAY_ORE = null;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(world.provider.getDimension()==0){
            this.generateOre(world,random,chunkX,chunkZ);
        }
    }
    private void generateOre(World world, Random random, int x, int z){
        int genZ,genY,genX;
        for (int i=0; i< clayOreVeinNumber; ++i){
            genX = x*16 + random.nextInt(16);
            genY = clayOreVeinMinY + random.nextInt(clayOreVeinMaxY-clayOreVeinMinY);
            genZ = z*16 + random.nextInt(16);
            new WorldGenMinable(CLAY_ORE.getDefaultState(), clayOreVeinSize, input -> input.getBlock() instanceof BlockStone).generate(world, random, new BlockPos(genX, genY, genZ));
            if (!generateDenseClayOreVein) continue;
            new WorldGenMinable(DENSE_CLAY_ORE.getDefaultState(), denseClayOreVeinSize,input -> input.getBlock() instanceof BlockClayOre).generate(world, random, new BlockPos(genX, genY, genZ));
            BlockPos pos = new BlockPos(genX + 8, genY, genZ + 8);
            if (world.getBlockState(pos).getBlock() != CLAY_ORE || world.getBlockState(pos).getBlock() != DENSE_CLAY_ORE || random.nextInt(2) != 0) continue;
            world.setBlockState(pos, LARGE_DENSE_CLAY_ORE.getDefaultState(), 2);
        }
        for (int i = 0; i < largeDenseClayOreVeinNumber; ++i) {
            genX = x*16 + random.nextInt(16);
            genY = largeDenseClayOreVeinMinY + random.nextInt(largeDenseClayOreVeinMaxY - largeDenseClayOreVeinMinY);
            genZ = z*16 + random.nextInt(16);
            new WorldGenMinable(LARGE_DENSE_CLAY_ORE.getDefaultState(), largeDenseClayOreVeinSize, input -> input.getBlock() instanceof BlockStone).generate(world, random, new BlockPos(genX, genY, genZ));
        }
    }

}
