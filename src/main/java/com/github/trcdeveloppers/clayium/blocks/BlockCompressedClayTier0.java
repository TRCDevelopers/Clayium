package com.github.trcdeveloppers.clayium.blocks;

import com.github.trcdeveloppers.clayium.annotation.Block;
import com.github.trcdeveloppers.clayium.items.IClayEnergy;
import com.github.trcdeveloppers.clayium.items.ITieredItem;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Block(registryName = "compressed_clay_tier0")
public class BlockCompressedClayTier0 extends ClayiumBlocks.ClayiumBlock implements IClayEnergy, ITieredItem {
    public BlockCompressedClayTier0(Material material) {
        super(material);
        this.setCreativeTab(CLAYIUM);
        this.setLightLevel(0f);
        this.setHarvestLevel("shovel", 0);
        this.setHardness(1f);
        this.setSoundType(SoundType.SAND);
    }
    public BlockCompressedClayTier0(){
        this(Material.ROCK);
    }

    @Override
    public long getClayEnergy() {
        return this.getTier()>=4 ? (long)Math.pow(10,this.getTier()+1) : 0L;
    }

    @Override
    public int getTier() {
        return 0;
    }
}
