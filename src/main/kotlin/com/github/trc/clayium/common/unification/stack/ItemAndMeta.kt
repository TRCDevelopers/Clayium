package com.github.trc.clayium.common.unification.stack

import com.github.trc.clayium.api.util.getAsItem
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer

fun PacketBuffer.writeItemAndMeta(itemAndMeta: ItemAndMeta) {
    writeString(itemAndMeta.item.registryName.toString())
    writeVarInt(itemAndMeta.meta)
}

fun PacketBuffer.readItemAndMeta(): ItemAndMeta {
    val item = Item.getByNameOrId(readString(Short.MAX_VALUE.toInt())) ?: throw IllegalArgumentException("Invalid item")
    val meta = readVarInt()
    return ItemAndMeta(item, meta)
}

data class ItemAndMeta(
    val item: Item,
    val meta: Int = 0,
) {
    constructor(block: Block, meta: Int = 0) : this(block.getAsItem(), meta)
    constructor(itemStack: ItemStack) : this(itemStack.item, itemStack.metadata)

    fun asStack(amount: Int = 1) = ItemStack(item, amount, meta)
}