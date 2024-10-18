package com.github.trc.clayium.api.unification.stack

import com.github.trc.clayium.api.W
import net.minecraft.item.ItemStack

/*
 * copied from `gregtech.api.unification.stack.ItemVariantMap`
 */

/** A map-like structure that stores values associated with item metadata. */
interface ItemVariantMap<E> {
    val isEmpty: Boolean
        get() = !hasWildcardEntry() && !hasNonWildcardEntry()

    /**
     * @param meta Item metadata
     * @return `true` if there's a nonnull value associated with given item metadata, `false`
     *   otherwise.
     */
    fun has(meta: Short): Boolean

    operator fun get(meta: Short): E?

    /**
     * @return `true` if there's any nonnull value associated with some item metadata, excluding
     *   metadata of [net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE] `(32767)`.
     */
    fun hasNonWildcardEntry(): Boolean

    /**
     * @return `true` if there's a nonnull value associated with metadata of
     *   [net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE] `(32767)`.
     */
    fun hasWildcardEntry(): Boolean = has(W.toShort())

    fun has(stack: ItemStack): Boolean = has(stack.itemDamage.toShort())

    fun get(stack: ItemStack): E? = get(stack.itemDamage.toShort())
}

interface MutableItemVariantMap<E> : ItemVariantMap<E> {
    /** Discard all associated value contained in this variant map. */
    fun clear()

    operator fun set(meta: Short, value: E?): E?

    operator fun set(stack: ItemStack, value: E?): E? = set(stack.itemDamage.toShort(), value)

    fun put(meta: Short, value: E?): E? = set(meta, value)

    fun put(stack: ItemStack, value: E?): E? = put(stack.itemDamage.toShort(), value)

    fun computeIfAbsent(meta: Short, supplier: () -> E): E {
        val current = get(meta)
        if (current != null) return current
        val value = supplier()
        if (value != null) put(meta, value)
        return value
    }
}

object EmptyItemVariantMap : ItemVariantMap<Nothing> {
    override fun has(meta: Short): Boolean = false

    override fun get(meta: Short): Nothing? = null

    override fun hasNonWildcardEntry(): Boolean = false

    override fun toString(): String = "EmptyItemVariantMap"
}
