package com.github.trcdeveloppers.clayium.blocks.machines;


import com.github.trcdeveloppers.clayium.Clayium;
import com.github.trcdeveloppers.clayium.annotation.CBlock;
import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.blocks.machines.tile.TileClayWorkTable;
import com.github.trcdeveloppers.clayium.gui.GuiHandler;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import com.github.trcdeveloppers.clayium.util.UtilLocale;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

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
    @ParametersAreNonnullByDefault
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
            System.out.println("This is remote");
            return true;
        }
        TileClayWorkTable te = (TileClayWorkTable) worldIn.getTileEntity(pos);
        if (te == null) {
            System.out.println("tileentity is null");
            return false;
        }
        IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
        playerIn.openGui(Clayium.INSTANCE, GuiHandler.ClayWorkTable, worldIn, pos.getX(), pos.getY(), pos.getZ());
        System.out.println("normal call");
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }
}
