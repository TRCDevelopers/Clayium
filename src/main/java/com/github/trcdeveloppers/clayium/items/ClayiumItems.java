package com.github.trcdeveloppers.clayium.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

public class ClayiumItems {
    private static final Map<String, Item> itemMap = new HashMap<>();
    public static Item getItem(String registryName){
        return itemMap.get(registryName);
    }
    public static void register(){
        //参考 https://blog1.mammb.com/entry/2015/03/31/001620
        String resourceName = "com/github/trcdeveloppers/clayium/items";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(resourceName);
        List<Class<?>> classes = new ArrayList<>();
        if (url!=null && url.getProtocol().equals("jar")){
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
                    if(an instanceof com.github.trcdeveloppers.clayium.annotation.Item){
                        Item it;
                        try {
                            it = (Item) c.newInstance();
                            it.setTranslationKey(((com.github.trcdeveloppers.clayium.annotation.Item) an).registryName())
                                    .setRegistryName(new ResourceLocation(MOD_ID, ((com.github.trcdeveloppers.clayium.annotation.Item) an).registryName()));
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        ForgeRegistries.ITEMS.register(it);
                        itemMap.put(((com.github.trcdeveloppers.clayium.annotation.Item) an).registryName(),it);
                        if(it instanceof ClayiumItem){
                            for(String oreDictionary : ((ClayiumItem) it).getOreDictionaries()){
                                OreDictionary.registerOre(oreDictionary, it);
                            }
                        }
                        if(FMLCommonHandler.instance().getSide().isClient()) {
                            if(it instanceof ClayiumItem && ((ClayiumItem) it).hasMetadata()) {
                                for(Map.Entry<Integer, String> st : ((ClayiumItem) it).getMetadataModels().entrySet()) {
                                    registerModel(it, st.getKey());
                                }
                            }else{
                                registerModel(it,0);
                            }
                        }
                    }
                }
            }
        }

    }
    public static abstract class ClayiumItem extends Item{
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

    @SideOnly(Side.CLIENT)
    public static void registerModel(Item i, int meta){
        if(i.getRegistryName() != null) net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(i,meta,new ModelResourceLocation(i.getRegistryName(),"inventory"));
    }
}
