package io.github.trcdevelopers.clayium.api.gui.sync

import com.cleanroommc.modularui.value.sync.GenericSyncValue
import io.github.trcdevelopers.clayium.api.gui.sync.codec.ClayLaserByteBufAdapter
import io.github.trcdevelopers.clayium.api.laser.ClayLaser
import org.jetbrains.annotations.ApiStatus
import java.util.function.Consumer
import java.util.function.Supplier

@Suppress("DEPRECATION")
@Deprecated("This class depends on ModularUI APIs that are scheduled for removal in version 3.2.0. Use `GenericSyncValue.builder()` directly instead. Refer to the `useBuilder` method in this class for a migration example.",
    ReplaceWith("GenericSyncValue.builder(ClayLaser::class.java)"),
)
@ApiStatus.ScheduledForRemoval(inVersion = "1.0.0.0")
class ClayLaserSyncValue(
    private val getter: Supplier<ClayLaser?>,
    private val setter: Consumer<ClayLaser?>,
) : GenericSyncValue<ClayLaser?>(
    ClayLaser::class.java,
    getter,
    setter,
    ClayLaserByteBufAdapter.INSTANCE,
    null,
    true,
) {

    fun useBuilder(): GenericSyncValue<ClayLaser?> {
        return GenericSyncValue.builder(ClayLaser::class.java)
            .getter(this.getter)
            .setter(this.setter)
            .adapter(ClayLaserByteBufAdapter.INSTANCE)
            .build()
    }

}