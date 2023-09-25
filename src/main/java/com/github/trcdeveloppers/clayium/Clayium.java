package com.github.trcdeveloppers.clayium;

import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.blocks.machines.clayworktable.ClayWorkTableRecipes;
import com.github.trcdeveloppers.clayium.blocks.machines.clayworktable.TileClayWorkTable;
import com.github.trcdeveloppers.clayium.gui.GuiHandler;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import com.github.trcdeveloppers.clayium.worldgen.ClayOreGenerator;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
        modid = Clayium.MOD_ID,
        name = Clayium.MOD_NAME,
        version = Clayium.VERSION
)
public class Clayium {

    public static final String MOD_ID = "clayium";
    public static final String MOD_NAME = "Clayium";
    public static final String VERSION = "1.0-SNAPSHOT";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static Clayium INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ClayiumBlocks.register();
        ClayiumItems.register();
        GameRegistry.registerWorldGenerator(new ClayOreGenerator(),0);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileClayWorkTable.class, new ResourceLocation(MOD_ID, "block_clayworktable"));
        ClayiumItems.getAllItems().values().forEach(item -> {
            if (item instanceof IItemColor) {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor) item, item);
            }
        });
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent
            public void onTooltip(ItemTooltipEvent e) {
                if (e.getItemStack().getItem() instanceof ITiered) {
                    e.getToolTip().set(0, ((ITiered) e.getItemStack().getItem()).getRarityColor().getColor().toString() + e.getToolTip().get(0));
                    e.getToolTip().add(1, "§rTier " + ((ITiered) e.getItemStack().getItem()).getTier());
                }
            }
        });

        ClayWorkTableRecipes.init();
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }
}
