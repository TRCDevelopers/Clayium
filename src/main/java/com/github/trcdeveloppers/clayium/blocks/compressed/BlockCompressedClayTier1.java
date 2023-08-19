package com.github.trcdeveloppers.clayium.blocks.compressed;

import com.github.trcdeveloppers.clayium.annotation.Block;
import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.interfaces.IClayEnergy;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Block(registryName = "compressed_clay_tier1")
public class BlockCompressedClayTier1 extends ClayiumBlocks.ClayiumBlock implements IClayEnergy, ITiered {
    public BlockCompressedClayTier1(Material material) {
        super(material);
        this.setCreativeTab(CLAYIUM);
        this.setLightLevel(0f);
        this.setHarvestLevel("shovel", 0);
        this.setHardness(0.5f);
        this.setSoundType(SoundType.GROUND);
    }

    @SuppressWarnings("unused")
    public BlockCompressedClayTier1() {
        this(Material.GROUND);
    }

    @Override
    public long getClayEnergy() {
        return this.getTier() >= 4 ? (long) Math.pow(10, this.getTier() + 1) : 0L;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(UtilLocale.ClayEnergyNumeral(this.getClayEnergy()) + "CE");

    }
}
