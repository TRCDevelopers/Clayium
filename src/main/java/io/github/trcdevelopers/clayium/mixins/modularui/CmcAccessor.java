package io.github.trcdevelopers.clayium.mixins.modularui;

import com.cleanroommc.modularui.test.CraftingModularContainer;
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper;
import com.cleanroommc.modularui.widgets.slot.ModularCraftingSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingModularContainer.class, remap = false)
public interface CmcAccessor {
    @Accessor
    InventoryCraftingWrapper getInventoryCrafting();

    @Accessor
    ModularCraftingSlot getCraftingSlot();
}
