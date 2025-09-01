package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.recipe.IRecipeOutputs
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandler

class SimpleRecipeOutputs @JvmOverloads constructor(
    stacks: List<ItemStack> = emptyList(),
) : IRecipeOutputs {

    constructor(vararg stacks: ItemStack) : this(listOf(*stacks))

    var stacks: List<ItemStack> = stacks
        private set
    override val type = TYPE

    override fun canFit(outputInventory: IItemHandler): Boolean {
        return TransferUtils.insertToHandler(outputInventory, this.stacks, true)
    }

    override fun produceOutputs(outputInventory: IItemHandler) {
        TransferUtils.insertToHandler(outputInventory, this.stacks, false)
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = NBTTagCompound()
        CUtils.writeItems(stacks, "outputs", data)
        return data
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        stacks = CUtils.readItems("outputs", nbt)
    }

    companion object {
        val TYPE = clayiumId("simple")
    }
}