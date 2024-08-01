package com.github.trc.clayium.common.unification.stack

import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.unification.OreDictUnifier
import com.github.trc.clayium.common.unification.material.Material
import com.github.trc.clayium.common.unification.ore.OrePrefix
import net.minecraft.block.Block
import net.minecraft.init.Items
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
    constructor(orePrefix: OrePrefix, material: Material) : this(OreDictUnifier.get(orePrefix, material))

    init {
        require(item !== Items.AIR) { "Empty ItemAndMeta is not allowed" }
        require(meta in 0..Short.MAX_VALUE) { "Invalid meta: $meta" }
    }

    fun asStack(amount: Int = 1) = ItemStack(item, amount, meta)
}