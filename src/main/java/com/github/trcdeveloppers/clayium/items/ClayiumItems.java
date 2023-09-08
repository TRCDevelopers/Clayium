package com.github.trcdeveloppers.clayium.items;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.annotation.CShape;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.util.OreDictUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

public class ClayiumItems {
    private static final Map<String, Item> items = new HashMap<>();

    public static Item getItem(String registryName) {
        return items.get(registryName);
    }

    public static Map<String, Item> getAllItems() {
        return items;
    }

    public static void register() {
        Field[] fields = CMaterials.class.getDeclaredFields();
        for (Field field : fields) {
            CItem cItem = field.getAnnotation(CItem.class);
            if (cItem == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                String registryName = cItem.registryName().isEmpty() ? field.getName().toLowerCase(Locale.ROOT) : cItem.registryName();
                Item item = (Item) field.get(null);
                registerItem(item, registryName);
                for (String oreDict : cItem.oreDicts()) {
                    OreDictionary.registerOre(oreDict, item);
                }
                registerShapes(item, cItem, registryName);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        //参考 https://blog1.mammb.com/entry/2015/03/31/001620
        String resourceName = "com/github/trcdeveloppers/clayium/items";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(resourceName);
        List<Class<?>> classes = new ArrayList<>();
        if (url != null && url.getProtocol().equals("jar")) {
            try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
                List<String> classPaths;
                classPaths = Collections.list(jarFile.entries()).stream()
                        .map(ZipEntry::getName)
                        .filter(name -> name.startsWith(resourceName))
                        .filter(name -> name.endsWith(".class"))
                        .map(name -> name.replace('/', '.').replaceAll(".class$", ""))
                        .collect(Collectors.toList());
                for (String p : classPaths) {
                    classes.add(classLoader.loadClass(p));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (Class<?> itemClass : classes) {
            CItem cItem = itemClass.getAnnotation(CItem.class);
            if (cItem == null) {
                continue;
            }
            // いつかリファクタリングする
            Item item;
            String registryName = cItem.registryName();
            try {
                item = (Item) itemClass.newInstance();
                item.setTranslationKey(registryName)
                        .setRegistryName(new ResourceLocation(MOD_ID, registryName));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            ForgeRegistries.ITEMS.register(item);
            items.put(registryName, item);
            for (String oreDict : cItem.oreDicts()) {
                OreDictionary.registerOre(oreDict, item);
            }
            registerShapes(item, cItem, registryName);
            // Register model for metas
            if (FMLCommonHandler.instance().getSide().isClient()) {
                if (item instanceof ClayiumItem && ((ClayiumItem) item).hasMetadata()) {
                    for (Map.Entry<Integer, String> st : ((ClayiumItem) item).getMetadataModels().entrySet()) {
                        registerModel(item, st.getKey());
                    }
                } else {
                    registerModel(item, 0);
                }
            }
        }
    }

    /***
     * @param item registryNameは、このメソッドでセットされるので、まだセットされていない必要がある。
     */
    private static void registerItem(Item item, String registryName) {
        item.setTranslationKey(registryName).setRegistryName(new ResourceLocation(MOD_ID, registryName));
        ForgeRegistries.ITEMS.register(item);
        registerModel(item, 0);
        items.put(registryName, item);
    }

    private static void registerShapes(Item item, CItem cItem, String registryName) {
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
            Item itemShaped = item instanceof CMaterialTiered
                ? new CMaterialTiered(((CMaterialTiered) item).getTier(), ((CMaterialTiered) item).getColors())
                : item instanceof CMaterial
                    ? new CMaterial(((CMaterial) item).getColors())
                    : new Item();

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

    public static class CItemTiered extends Item implements ITiered {
        private final int tier;

        public CItemTiered(int tier) {
            super.setCreativeTab(CLAYIUM);
            this.tier = tier;
        }

        @Override
        public int getTier() {
            return this.tier;
        }
    }

    public static class CMaterial extends Item implements IItemColor {
        private final int[] colors;

        public CMaterial(int... colors) {
            super();
            super.setCreativeTab(CLAYIUM);
            this.colors = colors;
        }

        @Override
        @ParametersAreNonnullByDefault
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            return tintIndex >= 0 && tintIndex < this.colors.length ? this.colors[tintIndex] : 0;
        }

        public int[] getColors() {
            return this.colors;
        }
    }
    public static class CMaterialTiered extends CMaterial implements ITiered, IItemColor {
        private final int tier;

        public CMaterialTiered(int tier, int... colors) {
            super(colors);
            this.tier = tier;
        }

        @Override
        public int getTier() {
            return this.tier;
        }
    }

}
