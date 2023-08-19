package com.github.trcdeveloppers.clayium.items;

import com.github.trcdeveloppers.clayium.annotation.Item;
import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Item(registryName = "clay_pickaxe")
public class ItemClayPickaxe extends ItemPickaxe {
    private float efficiencyOnClayOre = 32.0f;
    public ItemClayPickaxe(){
        super(ToolMaterial.STONE);
        this.setCreativeTab(CLAYIUM);
        this.setMaxDamage(500);

    }
    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
        if(this.isInCreativeTab(tab)){
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if(state.getBlock().getRegistryName() != null) {
            String regName = state.getBlock().getRegistryName().toString();
            if (regName.startsWith("clayium") && regName.endsWith("clay_ore")) {
                return state.getBlock().getHarvestLevel(state) <= stack.getItem().getHarvestLevel(stack, Objects.requireNonNull(state.getBlock().getHarvestTool(state)), null, state) ? this.efficiencyOnClayOre : this.efficiencyOnClayOre * 100f / 30f;
            }
        }
        return super.getDestroySpeed(stack,state);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack,worldIn,tooltip,flagIn);
        tooltip.add(I18n.format("item.clay_pickaxe.tooltip.line1"));
    }
}
