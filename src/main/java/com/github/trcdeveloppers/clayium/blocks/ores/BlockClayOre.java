package com.github.trcdeveloppers.clayium.blocks.ores;

import com.github.trcdeveloppers.clayium.annotation.Block;
import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.items.ItemClayPickaxe;
import com.github.trcdeveloppers.clayium.items.ItemClayShovel;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@Block(registryName = "clay_ore")
public class BlockClayOre extends ClayiumBlocks.ClayiumBlock {
    public BlockClayOre(Material material) {
        super(material);
        this.setCreativeTab(CLAYIUM);
        this.setLightLevel(0f);
        this.setHarvestLevel("pickaxe", 1);
        this.setResistance(5f);
        this.setHardness(3f);
        this.setSoundType(SoundType.STONE);
    }

    @SuppressWarnings("unused")
    public BlockClayOre() {
        this(Material.ROCK);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.CLAY_BALL;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            int i = random.nextInt(fortune + 2) - 1;
            if (i < 0) {
                i = 0;
            }
            return this.quantityDropped(random) * (i + 1);
        }
        return this.quantityDropped(random);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int quantityDropped(Random random) {
        return 4 + random.nextInt(5) * random.nextInt(4);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        if (player.getHeldItemMainhand().getItem() instanceof ItemClayShovel) return true;
        return super.canHarvestBlock(world, pos, player);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        player.addStat(Objects.requireNonNull(StatList.getBlockStats(this)));
        player.addExhaustion(0.005F);

        if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
            java.util.List<ItemStack> items = new java.util.ArrayList<>();
            ItemStack itemstack = this.getSilkTouchDrop(state);

            if (!itemstack.isEmpty()) {
                items.add(itemstack);
            }

            net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
            for (ItemStack item : items) {
                spawnAsEntity(worldIn, pos, item);
            }
        } else {
            harvesters.set(player);
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            // ClayShovel: +3 fortune, ClayPickaxe: +4 fortune.
            if (player.getHeldItemMainhand().getItem() instanceof ItemClayPickaxe) {
                i = (i + 1) * 4;
            } else if (player.getHeldItemMainhand().getItem() instanceof ItemClayShovel) {
                i = (i + 1) * 3;
            }
            this.dropBlockAsItem(worldIn, pos, state, i);
            harvesters.set(null);
        }
    }
}
