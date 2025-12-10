package io.github.trcdevelopers.clayium.api.sync

abstract class SyncedPropertyBase(
    private val syncManager: ClayiumSyncManager,
    private val index: Int,
) : ISyncedProperty {
    protected fun markDirty() {
        syncManager.markDirty(index)
    }
}