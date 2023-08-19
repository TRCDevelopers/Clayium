package com.github.trcdeveloppers.clayium.items;

import com.github.trcdeveloppers.clayium.annotation.Item;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Item(registryName = "clay_shovel")
public class ItemClayShovel extends ItemSpade {
    protected float efficiencyOnClayBlocks = 32.0f;
    private final float efficiencyOnClayOre = 12.0f;

    public ItemClayShovel() {
        super(ToolMaterial.WOOD);
        this.setMaxDamage(500);
        this.setCreativeTab(CLAYIUM);
    }

    @ParametersAreNonnullByDefault
    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (state.getBlock().getMaterial(state) == Material.CLAY) {
            return this.efficiencyOnClayBlocks;
        }
        if (state.getBlock().getRegistryName() != null) {
            String regName = state.getBlock().getRegistryName().toString();
            if (regName.startsWith("clayium") && regName.endsWith("clay_ore")) {
                return this.efficiencyOnClayOre;
            }
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (this.getRegistryName() == null) return;
        List<String> list = UtilLocale.localizeTooltip("item." + this.getRegistryName().getPath() + ".tooltip");
        if (list != null) {
            tooltip.addAll(list);
        }
    }
}
