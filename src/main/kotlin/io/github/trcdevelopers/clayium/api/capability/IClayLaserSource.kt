package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.api.laser.ClayLaser
import net.minecraft.util.EnumFacing
import org.jetbrains.annotations.ApiStatus

/**
 * Capability interface for blocks.
 * TODO: it's internally used ONLY in renderer. No need to be a capability.
 *
 * If you want to render a clay laser, please use [io.github.trcdevelopers.clayium.client.renderer.ClayLaserRenderer].
 */
@ApiStatus.ScheduledForRemoval(inVersion = "1.0.0.0")
@Deprecated("Not used in logic. Scheduled for removal in Release 1.0.0.0.")
interface IClayLaserSource {
    /**
     * The laser that is irradiating.
     * Null if deactivated.
     */
    val irradiatingLaser: ClayLaser?
    val direction: EnumFacing
    val length: Int
}