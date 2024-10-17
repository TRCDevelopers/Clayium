package com.github.trc.clayium.mixins.modularui;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// todo: delete this once it's fixed in ModularUI.
// open a PR to ModularUI to fix this.
@Mixin(ModularContainer.class)
public abstract class FixDupeWithPhantomSlotMixin {

    @Final
    @Shadow
    private List<ModularSlot> slots;

    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    public void clayium$slotClick(int slotId, int mouseButton, ClickType clickTypeIn, EntityPlayer player,
                                  CallbackInfoReturnable<ItemStack> cir) {
        if (!(clickTypeIn == ClickType.SWAP && mouseButton >= 0 && mouseButton < 9)) return;
        ModularSlot hotbar = this.slots.get(mouseButton);
        ModularSlot phantom = this.slots.get(slotId);
        if (!phantom.isPhantom()) return;

        phantom.putStack(hotbar.getStack());

        cir.setReturnValue(ItemStack.EMPTY);
        cir.cancel();
    }
}
