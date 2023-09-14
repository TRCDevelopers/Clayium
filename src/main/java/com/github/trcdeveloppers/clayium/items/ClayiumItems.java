package com.github.trcdeveloppers.clayium.items;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.annotation.CShape;
import com.github.trcdeveloppers.clayium.annotation.GeneralItemModel;
import com.github.trcdeveloppers.clayium.util.OreDictUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

public class ClayiumItems {
    private static final Map<String, Item> items = new HashMap<>();

    public static Item getItem(String registryName) {
        return items.get(registryName);
    }

    public static Map<String, Item> getAllItems() {
        return Collections.unmodifiableMap(items);
    }

    /***
     * @param item registryNameは、このメソッドでセットされるので、まだセットされていない必要がある。
     */
    public static void registerItem(Item item, String registryName) {
        item.setTranslationKey(registryName).setRegistryName(new ResourceLocation(MOD_ID, registryName));
        ForgeRegistries.ITEMS.register(item);
        items.put(registryName, item);
    }

    public static void registerShapes(Item item, CItem cItem, String registryName) {
        String materialName;
        if (!cItem.materialName().isEmpty()) {
            materialName = cItem.materialName();
        } else if (cItem.oreDicts().length != 0) {
            materialName = OreDictUtils.extractMaterialName(cItem.oreDicts()[0]);
        } else {
            materialName = registryName.replace("_ingot", "").replace("_dust", "");
        }
        for (CShape shape : cItem.shapes()) {
            // Create copy of an item
            GeneralItemModel model = GeneralItemModel.fromCShape(shape);
            Item itemShaped;
            if (item instanceof CMaterials.CMaterialTiered) {
                itemShaped = new CMaterials.CMaterialTiered(model, ((CMaterials.CMaterialTiered) item).getTier(), ((CMaterials.CMaterialTiered) item).getColors());
            } else if (item instanceof CMaterials.CMaterial) {
                itemShaped = new CMaterials.CMaterial(model, ((CMaterials.CMaterial) item).getColors());
            } else if (item instanceof CMaterials.CItemTiered) {
                itemShaped = new CMaterials.CItemTiered(((CMaterials.CItemTiered) item).getTier());
            } else {
                itemShaped = new Item().setCreativeTab(CLAYIUM);
            }

            String registryNameShaped = shape == CShape.LARGE_PLATE
                ? "large_" + materialName + "_plate"
                : materialName + "_" + shape.name().toLowerCase(Locale.ROOT);
            registerItem(itemShaped, registryNameShaped);
            if (cItem.oreDicts().length != 0) {
                OreDictionary.registerOre(shape.name().toLowerCase(Locale.ROOT)
                    + materialName.substring(0, 1).toUpperCase()
                    + materialName.substring(1), itemShaped);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModel(Item i, int meta) {
        if (i.getRegistryName() != null) {
            net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(i.getRegistryName(), "inventory"));
        }
    }

    public interface ClayiumItem {
        default boolean hasMetadata() {
            return false;
        }

        /* keyに設定されたメタデータに対してモデルをvalueに定義します。
         * hasMetadataがfalseの場合使用せず、メタデータ0に対してregistryNameの使用をします。
         */
        default Map<Integer, String> getMetadataModels() {
            return null;
        }

        default List<String> getOreDictionaries() {
            return Collections.emptyList();
        }
    }

}
