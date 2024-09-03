package com.github.trc.clayium.api.util.registry

import net.minecraft.util.registry.RegistryNamespaced

open class CRegistry<K, V>(
    /**
     * exclusive
     */
    private val maxId: Int,
) : RegistryNamespaced<K, V>() where K : Any, V : Any {
    override fun register(id: Int, key: K, value: V) {
        if (id < 0 || id >= maxId)
            throw IllegalArgumentException("Id is out of range: $id")
        getObjectById(id)?.let {
            throw IllegalArgumentException("Tried to reassign id $id to ($key: $value), but it is already assigned to (${getNameForObject(it)}: $it)") }
        super.register(id, key, value)
    }

    override fun putObject(key: K, value: V) {
        super.putObject(key, value)
    }

    fun getIdByKey(key: K): Int {
        return getObject(key)?.let { return getIDForObject(it) } ?: 0
    }
}