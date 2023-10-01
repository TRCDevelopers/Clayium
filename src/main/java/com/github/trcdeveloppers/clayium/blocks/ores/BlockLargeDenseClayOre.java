package com.github.trcdeveloppers.clayium.blocks.ores;

import com.github.trcdeveloppers.clayium.annotation.CBlock;
import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.items.ItemClayShovel;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@CBlock(registryName = "large_dense_clay_ore")
public class BlockLargeDenseClayOre extends Block implements ClayiumBlocks.ClayiumBlock {
    public BlockLargeDenseClayOre(Material material) {
        super(material);
        this.setCreativeTab(CLAYIUM);
        this.setLightLevel(0f);
        this.setHarvestLevel("pickaxe", 1);
        this.setResistance(5f);
        this.setHardness(3f);
        this.setSoundType(SoundType.STONE);
    }

    @SuppressWarnings("unused")
    public BlockLargeDenseClayOre() {
        this(Material.ROCK);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        if (player.getHeldItemMainhand().getItem() instanceof ItemClayShovel) return true;
        return super.canHarvestBlock(world, pos, player);
    }
}
