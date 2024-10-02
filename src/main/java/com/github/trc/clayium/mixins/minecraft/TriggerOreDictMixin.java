package com.github.trc.clayium.mixins.minecraft;

import com.github.trc.clayium.common.advancements.triggers.ModTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(InventoryChangeTrigger.class)
public class TriggerOreDictMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void clayium$trigger(EntityPlayerMP player, InventoryPlayer inventory) {
        ModTriggers.INVENTORY_CHANGED_OREDICT
                .trigger(player, inventory);
    }
}
