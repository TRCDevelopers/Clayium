package com.github.trcdeveloppers.clayium.blocks;

import com.github.trcdeveloppers.clayium.interfaces.IClayEnergy;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@ParametersAreNonnullByDefault
public class CompressedClay extends Block implements IClayEnergy {

    private final int TIER;
    private final long CE;
    private final String CE_TOOLTIP;

    public static Map<String, Block> createBlocks() {
        Map<String, Block> blocks = new HashMap<>();
        for (int tier = 0; tier < 13; tier++) {
            String registryName = "compressed_clay_" + tier;
            long ce = tier > 3 ? (long) Math.pow(10, tier - 4) : 0L;
            blocks.put(registryName, new CompressedClay(tier, ce, registryName));
        }
        return blocks;
    }

    private CompressedClay(Material materialIn, int tier, long ce, String registryName) {
        super(materialIn);
        this.setCreativeTab(CLAYIUM);
        this.setTranslationKey(registryName);
        this.setRegistryName(new ResourceLocation(MOD_ID, registryName));
        this.setLightLevel(0f);
        this.setHarvestLevel("shovel", 0);
        this.setHardness(0.5f);
        this.setSoundType(SoundType.GROUND);
        this.TIER = tier;
        this.CE = ce;
        //todo: この10^5倍をうまくラップするようなリファクタリング
        this.CE_TOOLTIP = ChatFormatting.GRAY + UtilLocale.ClayEnergyNumeral(ce * 100000) + "CE";
    }

    private CompressedClay(int tier, long ce, String registryName) {
        this(Material.GROUND, tier, ce, registryName);
    }

    @Override
    public long getClayEnergy() {
        return this.CE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.set(0, ClayiumItems.getRarity(this.TIER).getColor() + tooltip.get(0));
        tooltip.add(ChatFormatting.RESET + "Tier " + this.TIER);
        tooltip.add(this.CE_TOOLTIP);
    }
}
