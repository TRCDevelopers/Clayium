package com.github.trcdeveloppers.clayium.blocks.ores;


import com.github.trcdeveloppers.clayium.annotation.Block;
import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Block(registryName = "dense_clay_ore")
public class BlockDenseClayOre extends ClayiumBlocks.ClayiumBlock {
    public BlockDenseClayOre(Material material) {
        super(material);
        this.setCreativeTab(CLAYIUM);
        this.setLightLevel(0f);
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(3f);
        this.setResistance(5f);
        this.setSoundType(SoundType.STONE);
    }

    @SuppressWarnings("unused")
    public BlockDenseClayOre() {
        this(Material.ROCK);
    }
}
