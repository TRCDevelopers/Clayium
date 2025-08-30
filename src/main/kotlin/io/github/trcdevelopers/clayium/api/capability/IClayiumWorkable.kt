package io.github.trcdevelopers.clayium.api.capability

/**
 * Capability interface for TileEntities.
 */
interface IClayiumWorkable {
    val currentProgress: Int
    val requiredProgress: Int
    val isWorking: Boolean
}