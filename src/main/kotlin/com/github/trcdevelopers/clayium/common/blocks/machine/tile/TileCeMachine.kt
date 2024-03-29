package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.clayenergy.IEnergizedClay
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasLong
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.ItemStackHandler

abstract class TileCeMachine : TileMachine() {

    val ceSlot = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            markDirty()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.item is IEnergizedClay
        }

        override fun getSlotLimit(slot: Int): Int {
            //todo upgradable
            return 1
        }
    }

    var storedCe: ClayEnergy = ClayEnergy.of(0)

    override fun onBlockPlaced(player: EntityLivingBase, stack: ItemStack) {
        super.onBlockPlaced(player, stack)
        _inputs[EnumFacing.UP.index] = MachineIoMode.ALL
        _inputs[player.horizontalFacing.index] = MachineIoMode.CE
        _outputs[EnumFacing.DOWN.index] = MachineIoMode.ALL
    }

    override fun update() {
        importEnergizedClay()
        super.update()
    }

    protected fun isCeImporting(side: EnumFacing): Boolean {
        return _inputs[side.index] == MachineIoMode.CE
    }

    private fun importEnergizedClay() {
        val ceSlotLimit = ceSlot.getSlotLimit(0)
        if (ceSlot.getStackInSlot(0).count >= ceSlotLimit) return
        for (side in EnumFacing.entries) {
            if (isCeImporting(side)) {
                val from = world.getTileEntity(pos.offset(side))?.getCapability(ITEM_HANDLER_CAPABILITY, side.opposite) ?: return
                for (i in 0..<from.slots) {
                    val stack = from.extractItem(i, ceSlotLimit, true)
                    if (stack.isEmpty) continue
                    if (ceSlot.insertItem(0, stack, true).count < stack.count) {
                        ceSlot.insertItem(0, from.extractItem(i, ceSlotLimit, false), false)
                        break
                    }
                }
            }
        }
    }

    fun extractCe(): Boolean {
        val itemStack = ceSlot.getStackInSlot(0)
        if (itemStack.isEmpty) return false
        val item = itemStack.item
        if (item !is IEnergizedClay) return false

        storedCe += item.getClayEnergy(itemStack)
        ceSlot.extractItem(0, 1, false)
        return true
    }

    /**
     * Consume the given ce. Returns true if the machine has enough ClayEnergy and was successfully consumed.
     * If the machine lacks ClayEnergy, it will try to extract CE from ceSlot, and if the generation is successful, it will recurse to this method.
     * @return true if [ce] is consumed, false otherwise.
     */
    fun tryConsumeCe(ce: ClayEnergy): Boolean {
        if (storedCe < ce) {
            if (extractCe()) {
                return tryConsumeCe(ce)
            }
            return false
        }

        storedCe -= ce
        return true
    }

    override fun getDrops(): List<ItemStack> {
        val drops = mutableListOf<ItemStack>()
        drops.addAll(super.getDrops())
        val ceSlotStack = ceSlot.getStackInSlot(0)
        if (!ceSlotStack.isEmpty) {
            drops.add(ceSlotStack)
        }
        return drops
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setLong("stored_ce", storedCe.energy)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        storedCe = if (compound.hasLong("stored_ce")) ClayEnergy(compound.getLong("stored_ce")) else ClayEnergy.of(0)
        super.readFromNBT(compound)
    }
}