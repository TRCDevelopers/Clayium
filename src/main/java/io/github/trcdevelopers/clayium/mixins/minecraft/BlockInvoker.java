package io.github.trcdevelopers.clayium.mixins.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockInvoker {
    @Invoker
    ItemStack callGetSilkTouchDrop(IBlockState state);
}
