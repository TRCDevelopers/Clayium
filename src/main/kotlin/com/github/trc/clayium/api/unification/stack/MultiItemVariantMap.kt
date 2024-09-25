package com.github.trc.clayium.api.unification.stack

import com.github.trc.clayium.api.W
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap

/**
 * For subtyped items.
 * Uses a map internally to store separate values for each metadata.
 * Also holds a wildcard value.
 */
class MultiItemVariantMap<E> : MutableItemVariantMap<E> {

    private val map by lazy { Short2ObjectOpenHashMap<E>() }
    private var wildcardValue: E? = null

    override fun clear() {
        wildcardValue = null
        map.clear()
    }

    override fun set(meta: Short, value: E?): E? {
        if (meta == W.toShort()) {
            val old = wildcardValue
            wildcardValue = value
            return old
        } else {
            return if (value == null) {
                map.remove(meta)
            } else {
                map.put(meta, value)
            }
        }
    }

    override fun has(meta: Short): Boolean {
        return (meta == W.toShort() && wildcardValue != null) || (map.containsKey(meta))
    }

    override fun get(meta: Short): E? {
        return if (meta == W.toShort()) wildcardValue else map[meta]

    }

    override fun hasNonWildcardEntry(): Boolean {
        return map.isNotEmpty()
    }

    override fun hasWildcardEntry(): Boolean {
        return wildcardValue != null
    }
}