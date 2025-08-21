package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.item.IClayFluidCapsule
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.items.ItemFluidCapsule
import io.github.trcdevelopers.clayium.common.util.FluidStackUtils
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import io.github.trcdevelopers.clayium.common.util.copy
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min

class ClayFluidCapsuleBackedItemFluidHandler(
    notifiable: IMarkDirty,
    itemInventorySize: Int,
) : ClayiumItemStackHandler(notifiable, itemInventorySize), IFluidHandler {

    private var dirty: Boolean = false
    private val fluidToAmount = Object2IntOpenHashMap<Fluid>()
    private val maxFluidCapacity = this.slots * 64 * ItemFluidCapsule.MAX_CAPACITY

    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        this.dirty = true
    }

    override fun getTankProperties(): Array<out IFluidTankProperties> {
        refreshFluidIfRequired()
        return this.fluidToAmount.map { (fluid, amount) ->
            FluidTankProperties(FluidStack(fluid, amount), amount, true, true)
        }.toTypedArray()
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        this.dirty = this.dirty || doFill
        val itemStacks = FluidStackUtils.toCapsules(resource, this.slots * 64 * ItemFluidCapsule.MAX_CAPACITY)
        return insertCapsulesTo(itemStacks, this, simulate = !doFill)
    }

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        this.refreshFluidIfRequired()
        val drained = this.extractFluid(resource.fluid, resource.amount, simulate = !doDrain)
        if (drained <= 0) return null
        return FluidStack(resource.fluid, drained)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        this.refreshFluidIfRequired()
        val fluid = this.fluidToAmount.keys.firstOrNull() ?: return null
        return this.drain(FluidStack(fluid, maxDrain), doDrain)
    }

    private fun refreshFluidIfRequired() {
        if (!this.dirty) return
        val fluidToAmount = Object2IntOpenHashMap<Fluid>()
        for (i in 0..<this.slots) {
            val stack = this.getStackInSlot(i)
            if (stack.isEmpty) continue
            val capsuleItem = stack.item as? IClayFluidCapsule ?: continue
            val fluidStack = capsuleItem.getFluid(stack) ?: continue
            val amount = fluidStack.amount * stack.count
            fluidToAmount.addTo(fluidStack.fluid, amount)
        }
        this.fluidToAmount.clear()
        this.fluidToAmount.putAll(fluidToAmount)
    }

    private fun extractFluid(
        fluid: Fluid,
        amount: Int,
        simulate: Boolean,
    ) : Int {
        val stored = this.fluidToAmount.getInt(fluid)
        if (stored <= 0) return 0

        if (simulate) {
            return min(stored, amount)
        }

        val isEmptied = stored <= amount
        val capsules = this.capsules(fluid)
        if (isEmptied) {
            for (slot in capsules) {
                this.extractItem(slot, Int.MAX_VALUE, false)
            }
            this.dirty = true
            this.fluidToAmount.removeInt(fluid)
            return amount
        } else {
            val copied = this.copy()
            val remainAmount = stored - amount
            val remainCapsules = FluidStackUtils.toCapsules(FluidStack(fluid, remainAmount), this.maxFluidCapacity)
            for (slot in capsules) {
                copied.setStackInSlot(slot, ItemStack.EMPTY)
            }
            val allInserted = TransferUtils.insertToHandler(copied, remainCapsules)
            if (!allInserted) return 0

            this.dirty = true
            this.fluidToAmount.put(fluid, remainAmount)
            for (i in 0..<this.slots) {
                this.setStackInSlot(i, copied.getStackInSlot(i))
            }
            return amount
        }
    }

    private fun capsules(fluid: Fluid): IntList {
        val list = IntArrayList()
        for (i in 0..<this.slots) {
            val stack = this.getStackInSlot(i)
            if (stack.isEmpty) continue
            val capsuleItem = stack.item as? IClayFluidCapsule ?: continue
            if (capsuleItem.getFluid(stack)?.fluid == fluid) {
                list.add(i)
            }
        }
        return list
    }

    /**
     * @return amount of fluid inserted
     */
    private fun insertCapsulesTo(
        capsules: List<ItemStack>,
        to: IItemHandler,
        simulate: Boolean,
    ): Int {
        var insertedFluidAmount = 0
        for (capsule in capsules) {
            val count = capsule.count
            val capsuleItem = capsule.item as? ItemFluidCapsule ?: continue
            val capsuleCapacity = FluidStackUtils.capsuleToAmount.getInt(capsuleItem)
            if (capsuleCapacity == FluidStackUtils.capsuleToAmount.defaultReturnValue()) continue

            val extracted = capsule.copyWithSize(count)
            val remain = ItemHandlerHelper.insertItem(to, extracted, simulate)
            if (remain.isEmpty) {
                insertedFluidAmount += extracted.count * capsuleCapacity
            } else {
                val inserted = extracted.count - remain.count
                insertedFluidAmount += inserted * capsuleCapacity
            }
        }
        return insertedFluidAmount
    }
}