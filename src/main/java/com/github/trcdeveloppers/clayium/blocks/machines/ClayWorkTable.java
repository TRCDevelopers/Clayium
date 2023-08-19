package com.github.trcdeveloppers.clayium.blocks.machines;


import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@com.github.trcdeveloppers.clayium.annotation.Block(registryName = "clay_work_table")
public class ClayWorkTable extends Block implements ITiered, ClayiumBlocks.ClayiumBlock {
    public ClayWorkTable() {
        super(Material.ROCK);
    }

    @Override
    public int getTier() {
        return 0;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (this.getRegistryName() == null) return;
        List<String> list = UtilLocale.localizeTooltip("tile." + this.getRegistryName().getPath() + ".tooltip");
        if (list != null) {
            tooltip.addAll(list);
        }
    }
}
