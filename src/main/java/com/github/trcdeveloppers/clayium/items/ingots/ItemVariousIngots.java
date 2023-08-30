package com.github.trcdeveloppers.clayium.items.ingots;

import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemVariousIngots {
    public static class CMaterial extends Item implements ClayiumItems.ClayiumItem, IItemColor {
        private final int[] colors;
        private final List<String> oreDicts;

        public CMaterial(int[] colors, String... oreDicts) {
            this.colors = colors;
            this.oreDicts = Arrays.asList(oreDicts);
        }

        public CMaterial(int[] colors) {
            this.colors = colors;
            this.oreDicts = Collections.emptyList();
        }

        @Override
        @ParametersAreNonnullByDefault
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            return tintIndex > 0 && tintIndex < colors.length ? this.colors[tintIndex] : 0;
        }

        @Override
        public List<String> getOreDictionaries() {
            return this.oreDicts;
        }

        public static class CMaterialTiered extends CMaterial implements ITiered {
            private final int tier;

            public CMaterialTiered(int[] colors, int tier, String... oreDicts) {
                super(colors, oreDicts);
                this.tier = tier;
            }

            public CMaterialTiered(int[] colors, int tier) {
                super(colors);
                this.tier = tier;
            }

            @Override
            public int getTier() {
                return this.tier;
            }
        }
    }
}
