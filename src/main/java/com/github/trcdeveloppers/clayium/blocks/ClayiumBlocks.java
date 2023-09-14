package com.github.trcdeveloppers.clayium.blocks;


import com.github.trcdeveloppers.clayium.annotation.CBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;
import static com.github.trcdeveloppers.clayium.items.ClayiumItems.registerModel;

public class ClayiumBlocks {
    private static final Map<String, Block> blockMap = new HashMap<>();

    public static void register() {
        String resourceName = "com/github/trcdeveloppers/clayium/blocks";
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
        for (Class<?> clazz : classes) {
            CBlock cBlock = clazz.getAnnotation(CBlock.class);
            if (cBlock == null) {
                continue;
            }
            Block block;
            try {
                block = ((Block) clazz.newInstance()).setCreativeTab(CLAYIUM)
                    .setTranslationKey(cBlock.registryName())
                    .setRegistryName(new ResourceLocation(MOD_ID, cBlock.registryName()));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            ForgeRegistries.BLOCKS.register(block);
            blockMap.put(cBlock.registryName(), block);
            ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(cBlock.registryName()));
            if (block instanceof ClayiumBlock) {
                for (String oreDictionary : ((ClayiumBlock) block).getOreDictionaries()) {
                    OreDictionary.registerOre(oreDictionary, Item.getItemFromBlock(block));
                }
            }
            if (FMLCommonHandler.instance().getSide().isClient()) {
                if (block instanceof ClayiumBlocks.ClayiumBlock && ((ClayiumBlocks.ClayiumBlock) block).hasMetadata()) {
                    for (Map.Entry<Integer, String> st : ((ClayiumBlocks.ClayiumBlock) block).getMetadataModels().entrySet()) {
                        registerModel(Item.getItemFromBlock(block), st.getKey());
                    }
                } else {
                    registerModel(Item.getItemFromBlock(block), 0);
                }
            }
        }
    }

    public static Block getBlock(String registryName) {
        return blockMap.get(registryName);
    }

    public interface ClayiumBlock {
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
            return new ArrayList<>();
        }
    }
}
