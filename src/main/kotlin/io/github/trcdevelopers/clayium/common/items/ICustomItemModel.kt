package io.github.trcdevelopers.clayium.common.items

import org.jetbrains.annotations.ApiStatus

/**
 * Register items implementing this interface through the [io.github.trcdevelopers.clayium.client.ClientProxy]
 * to call [registerModels] method.
 *
 * NOTE: This is an internal interface.
 */
@ApiStatus.Internal
interface ICustomItemModel {
    fun registerModels()
}