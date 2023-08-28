package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.annotation.MaterialFor;
import com.github.trcdeveloppers.clayium.annotation.MaterialTypes;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@MaterialFor(materialName = "silicon", materialFor = {MaterialTypes.PLATE, MaterialTypes.LARGE_PLATE, MaterialTypes.DUST})
@CItem(registryName = "silicon_ingot")
public class itemSiliconIngot extends Item implements ClayiumItems.ClayiumItem, IItemColor, ITiered {

    public itemSiliconIngot() {
        super();
        setCreativeTab(CLAYIUM);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(Objects.requireNonNull(UtilLocale.localizeTooltip("item.silicon_ingot.tooltip")));
    }

    @Override
    public List<String> getOreDictionaries() {
        return new ArrayList<String>() {{
            add("ingotSilicon");
        }};
    }

    @Override
    @ParametersAreNonnullByDefault
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? 0x281C28 : (tintIndex == 1 ? 0x191919 : 0xFFFFFF);
    }

    @Override
    public int getTier() {
        return 6;
    }
}