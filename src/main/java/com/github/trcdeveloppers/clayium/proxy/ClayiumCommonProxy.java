package com.github.trcdeveloppers.clayium.proxy;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.items.CMaterials;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;


public class ClayiumCommonProxy {
    public void registerItems() {
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
                ClayiumItems.registerItem(item, registryName);
                for (String oreDict : cItem.oreDicts()) {
                    OreDictionary.registerOre(oreDict, item);
                }
                ClayiumItems.registerShapes(item, cItem, registryName);
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
                ClayiumItems.registerItem(item, registryName);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (String oreDict : cItem.oreDicts()) {
                OreDictionary.registerOre(oreDict, item);
            }
            ClayiumItems.registerShapes(item, cItem, registryName);
        }
    }
}
