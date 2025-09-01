package io.github.trcdevelopers.clayium.api.capability

/**
 * Capability interface for TileEntities.
 */
interface IClayiumWorkable {
    val currentProgress: Long
    val requiredProgress: Long
    val isWorking: Boolean
}