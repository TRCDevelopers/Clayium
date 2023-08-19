package com.github.trcdeveloppers.clayium.worldgen;

import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.blocks.ores.BlockClayOre;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

//all codes are from clayium and make these work same in 1.12.2
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
            new WorldGenMinable(ClayiumBlocks.getBlock("clay_ore").getDefaultState(), clayOreVeinSize, input -> input.getBlock() instanceof BlockStone).generate(world, random, new BlockPos(genX, genY, genZ));
            if (!generateDenseClayOreVein) continue;
            new WorldGenMinable(ClayiumBlocks.getBlock("dense_clay_ore").getDefaultState(), denseClayOreVeinSize,input -> input.getBlock() instanceof BlockClayOre).generate(world, random, new BlockPos(genX, genY, genZ));
            if (world.getBlockState(new BlockPos(genX + 8, genY, genZ + 8)).getBlock() != ClayiumBlocks.getBlock("clay_ore") || world.getBlockState(new BlockPos(genX + 8, genY, genZ + 8)).getBlock() != ClayiumBlocks.getBlock("dense_clay_ore") || random.nextInt(2) != 0) continue;
            world.setBlockState(new BlockPos(genX + 8, genY, genZ + 8), ClayiumBlocks.getBlock("large_dense_clay_ore").getDefaultState(),2);
        }
        for (int i = 0; i < largeDenseClayOreVeinNumber; ++i) {
            genX = x*16 + random.nextInt(16);
            genY = largeDenseClayOreVeinMinY + random.nextInt(largeDenseClayOreVeinMaxY - largeDenseClayOreVeinMinY);
            genZ = z*16 + random.nextInt(16);
            new WorldGenMinable(ClayiumBlocks.getBlock("large_dense_clay_ore").getDefaultState(), largeDenseClayOreVeinSize, inpt -> inpt.getBlock() instanceof BlockStone).generate(world, random, new BlockPos(genX, genY, genZ));
        }
    }

}
