package com.github.trcdeveloppers.clayium.common.blocks;


import com.github.trcdeveloppers.clayium.common.annotation.CBlock;
import com.google.common.reflect.ClassPath;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static com.github.trcdeveloppers.clayium.common.creativetab.ClayiumCreativeTab.CLAYIUM;

public class ClayiumBlocks {
    private static final Map<String, Block> blocks = new HashMap<>();

    public static Block getBlock(String registryName) {
        return blocks.get(registryName);
    }

    public static Map<String, Block> getAllBlocks() {
        return Collections.unmodifiableMap(blocks);
    }

    public static void registerBlocks(RegistryEvent.Register<Block> event, Side side) {
        //参考 https://blog1.mammb.com/entry/2015/03/31/001620
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassPath.from(classLoader)
                .getTopLevelClassesRecursive("com.github.trcdeveloppers.clayium.common.blocks").stream()
                .map(ClassPath.ClassInfo::load)
                .forEach(clazz -> {
                    CBlock cBlock = clazz.getAnnotation(CBlock.class);
                    if (cBlock == null) {
                        return;
                    }
                    Block block;
                    try {
                        block = (Block) clazz.newInstance();
                        String registryName = cBlock.registryName();
                        block.setCreativeTab(CLAYIUM)
                            .setTranslationKey(registryName)
                            .setRegistryName(new ResourceLocation(MOD_ID, registryName));
                        blocks.put(registryName, block);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        blocks.putAll(CompressedClay.createBlocks());
        blocks.values().forEach(event.getRegistry()::register);
    }
}
