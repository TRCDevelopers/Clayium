package com.github.trcdeveloppers.clayium.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static com.github.trcdeveloppers.clayium.items.ClayiumItems.registerModel;

public class ClayiumBlocks {
    private static final Map<String, Block> blockMap = new HashMap<>();
    public static void register() {
        String resourceName = "com/github/trcdeveloppers/clayium/blocks";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(resourceName);
        List<Class<?>> classes = new ArrayList<>();
        if (url.getProtocol().equals("jar")){
            try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()){
                List<String> classPaths;
                classPaths= Collections.list(jarFile.entries()).stream()
                        .map(ZipEntry::getName)
                        .filter(name -> name.startsWith(resourceName))
                        .filter(name -> name.endsWith(".class"))
                        .map(name -> name.replace('/', '.').replaceAll(".class$", ""))
                        .collect(Collectors.toList());
                for (String p : classPaths){
                    classes.add(classLoader.loadClass(p));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for(Class<?> c : classes){
            Annotation[] ano;
            if((ano = c.getAnnotations()).length!=0){
                for(Annotation an : ano){
                    if(an instanceof com.github.trcdeveloppers.clayium.annotation.Block){
                        Block b;
                        try {
                            b = ((ClayiumBlock) c.newInstance()).setTranslationKey(((com.github.trcdeveloppers.clayium.annotation.Block) an).registryName())
                                    .setRegistryName(new ResourceLocation(MOD_ID, ((com.github.trcdeveloppers.clayium.annotation.Block) an).registryName()));
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        ForgeRegistries.BLOCKS.register(b);
                        blockMap.put(((com.github.trcdeveloppers.clayium.annotation.Block) an).registryName(),b);
                        ForgeRegistries.ITEMS.register(new ItemBlock(b).setRegistryName(((com.github.trcdeveloppers.clayium.annotation.Block) an).registryName()));
                        if(b instanceof ClayiumBlock) {
                            for (String oreDictionary : ((ClayiumBlock) b).getOreDictionaries()) {
                                OreDictionary.registerOre(oreDictionary, Item.getItemFromBlock(b));
                            }
                        }
                        if(FMLCommonHandler.instance().getSide().isClient()) {
                            if(b instanceof ClayiumBlocks.ClayiumBlock && ((ClayiumBlocks.ClayiumBlock) b).hasMetadata()) {
                                for(Map.Entry<Integer, String> st : ((ClayiumBlocks.ClayiumBlock) b).getMetadataModels().entrySet()) {
                                    registerModel(Item.getItemFromBlock(b), st.getKey(), st.getValue());
                                }
                            }else{
                                registerModel(Item.getItemFromBlock(b),0,((com.github.trcdeveloppers.clayium.annotation.Block) an).registryName());
                            }
                        }
                    }
                }
            }
        }
    }
    public static abstract class ClayiumBlock extends Block{
        public ClayiumBlock(Material materialIn) {
            super(materialIn);
        }
        public ClayiumBlock(){super(Material.CLOTH);}

        public boolean hasMetadata(){
            return false;
        }
        /* keyに設定されたメタデータに対してモデルをvalueに定義します。
         * hasMetadataがfalseの場合使用せず、メタデータ0に対してregistryNameの使用をします。
         */
        public Map<Integer, String> getMetadataModels(){
            return null;
        }
        public List<String> getOreDictionaries(){
            return new ArrayList<>();
        }
    }
    public static Block getBlock(String registryName){
        return blockMap.get(registryName);
    }
}
