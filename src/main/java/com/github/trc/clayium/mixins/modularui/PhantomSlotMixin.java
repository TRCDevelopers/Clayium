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

@Mixin(ModularContainer.class)
public class PhantomSlotMixin {
    @Final
    @Shadow
    private List<ModularSlot> slots;

    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    public void clayium$slotClick(int slotId, int mouseButton, ClickType clickTypeIn, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        if (clickTypeIn != ClickType.THROW) return;
        ModularSlot phantom = this.slots.get(slotId);
        if (phantom == null || !phantom.isPhantom()) return;

        if (phantom.getHasStack() && phantom.canTakeStack(player)) {
            ItemStack stack = phantom.decrStackSize(mouseButton == 0 ? 1 : phantom.getStack().getCount());
            phantom.onTake(player, stack);
        }

        cir.setReturnValue(ItemStack.EMPTY);
        cir.cancel();
    }
}
