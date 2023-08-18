package com.github.trcdeveloppers.clayium.blocks;

import com.github.trcdeveloppers.clayium.annotation.Block;
import com.github.trcdeveloppers.clayium.interfaces.IClayEnergy;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Block(registryName = "compressed_clay_tier0")
public class BlockCompressedClayTier0 extends ClayiumBlocks.ClayiumBlock implements IClayEnergy, ITiered {
    public BlockCompressedClayTier0(Material material) {
        super(material);
        this.setCreativeTab(CLAYIUM);
        this.setLightLevel(0f);
        this.setHarvestLevel("shovel",0);
        this.setHardness(0.5f);
        this.setSoundType(SoundType.GROUND);
    }
    public BlockCompressedClayTier0(){
        this(Material.GROUND);
    }

    @Override
    public long getClayEnergy() {
        return this.getTier()>=4 ? (long)Math.pow(10,this.getTier()+1) : 0L;
    }

    @Override
    public int getTier() {
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack,worldIn,tooltip,flagIn);
        tooltip.add(UtilLocale.ClayEnergyNumeral(this.getClayEnergy()) + "CE");

    }
}
