package com.github.trc.clayium.api.unification.stack

/** For non subtyped items. Holds one shard value for all metadata. */
class SingleItemVariantMap<E> : MutableItemVariantMap<E> {

    private var value: E? = null

    override fun clear() {
        value = null
    }

    override fun set(meta: Short, value: E?): E? {
        val old = this.value
        this.value = value
        return old
    }

    override fun has(meta: Short): Boolean {
        return value != null
    }

    override fun get(meta: Short): E? {
        return value
    }

    override fun hasNonWildcardEntry(): Boolean {
        return value != null
    }
}
