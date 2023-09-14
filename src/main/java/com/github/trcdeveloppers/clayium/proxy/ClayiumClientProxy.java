package com.github.trcdeveloppers.clayium.proxy;

import com.github.trcdeveloppers.clayium.annotation.GeneralItemModel;
import com.github.trcdeveloppers.clayium.items.CMaterials;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

import java.util.Locale;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

@SuppressWarnings("unused")
public class ClayiumClientProxy extends ClayiumCommonProxy {
    @Override
    public void registerItems() {
        super.registerItems();

        ClayiumItems.getAllItems().forEach((name, item) -> {
            if (item instanceof CMaterials.CMaterial) {
                registerGeneralModel(item, ((CMaterials.CMaterial) item).MODEL);
            } else {
                registerModel(item);
            }
        });
    }

    private static void registerModel(Item i) {
        if (i.getRegistryName() != null) {
            net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
        }
    }

    private static void registerGeneralModel(Item item, GeneralItemModel model) {
        net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(
            item,
            0,
            new ModelResourceLocation(MOD_ID + ":colored/" + model.name().toLowerCase(Locale.ROOT), "inventory")
        );
    }
}
