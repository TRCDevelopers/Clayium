package com.github.trcdeveloppers.clayium.blocks.machines.clayworktable;


import com.github.trcdeveloppers.clayium.Clayium;
import com.github.trcdeveloppers.clayium.annotation.CBlock;
import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.gui.GuiHandler;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@CBlock(registryName = "clay_work_table")
public class ClayWorkTable extends BlockContainer implements ITiered, ClayiumBlocks.ClayiumBlock {
    public ClayWorkTable() {
        super(Material.ROCK);
    }

    @Override
    public int getTier() {
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (this.getRegistryName() == null) return;
        List<String> list = UtilLocale.localizeTooltip("tile." + this.getRegistryName().getPath() + ".tooltip");
        if (list != null) {
            tooltip.addAll(list);
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileClayWorkTable();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        TileClayWorkTable te = (TileClayWorkTable) worldIn.getTileEntity(pos);
        if (te == null) {
            return false;
        }
        playerIn.openGui(Clayium.INSTANCE, GuiHandler.CLAY_WORK_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileClayWorkTable tile = (TileClayWorkTable) worldIn.getTileEntity(pos);
        if (tile != null) {
            IItemHandler handler = Objects.requireNonNull(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getStackInSlot(i).isEmpty()) {
                    continue;
                }
                float f0 = worldIn.rand.nextFloat() * 0.6f + 0.1f;
                float f1 = worldIn.rand.nextFloat() * 0.6f + 0.1f;
                float f2 = worldIn.rand.nextFloat() * 0.6f + 0.1f;
                EntityItem entityItem = new EntityItem(worldIn, pos.getX() + f0, pos.getY() + f1, pos.getZ() + f2, handler.getStackInSlot(i).copy());
                float f3 = 0.025f;
                entityItem.motionX = worldIn.rand.nextGaussian() * f3;
                entityItem.motionY = worldIn.rand.nextGaussian() * f3 + 0.1f;
                entityItem.motionZ = worldIn.rand.nextGaussian() * f3;
                worldIn.spawnEntity(entityItem);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
