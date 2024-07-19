package com.github.trc.clayium.common.pan.factories

import com.github.trc.clayium.api.pan.IPanEntry
import com.github.trc.clayium.api.pan.IPanEntryFactory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.Container
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

object CraftingTablePanEntryFactory : IPanEntryFactory {
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>): IPanEntry? {
        if (world.getBlockState(pos).block === Blocks.CRAFTING_TABLE || world !is World) return null

        val dummyContainer = object : Container() { override fun canInteractWith(playerIn: EntityPlayer) = false }
        val matrix = InventoryCrafting(dummyContainer, 3, 3)
        for (slot in 0..<9) matrix.setInventorySlotContents(slot, stacks[slot])

        val output = CraftingManager.findMatchingResult(matrix, world)
        return null
    }

}