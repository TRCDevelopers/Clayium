package com.github.trc.clayium.api.gui.sync.codec;

import com.cleanroommc.modularui.utils.serialization.IByteBufAdapter;
import com.cleanroommc.modularui.utils.serialization.IEquals;
import com.github.trc.clayium.api.laser.ClayLaser;
import com.github.trc.clayium.api.laser.ClayLaserKt;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link com.cleanroommc.modularui.utils.serialization.IEquals} has not null parameters,
 * but {@link ClayLaser} can be null.
 * so we have to use Java.
 */
public class ClayLaserByteBufAdapter implements IByteBufAdapter<@Nullable ClayLaser> {
    public static final ClayLaserByteBufAdapter INSTANCE = new ClayLaserByteBufAdapter();

    private ClayLaserByteBufAdapter() {}

    private final IEquals<@Nullable ClayLaser> equalsDelegate = IEquals.wrapNullSafe((ClayLaser::equals));

    @Override
    @Nullable
    public ClayLaser deserialize(@NotNull PacketBuffer buffer) {
        if (buffer.readBoolean()) {
            return ClayLaserKt.readClayLaser(buffer);
        } else {
            return null;
        }
    }

    @Override
    public void serialize(@NotNull PacketBuffer buffer, @Nullable ClayLaser u) {
        if (u != null) {
            buffer.writeBoolean(true);
            ClayLaserKt.writeClayLaser(buffer, u);
        } else {
            buffer.writeBoolean(false);
        }
    }

    @Override
    @SuppressWarnings("DataFlowIssue") // equalsDelegate is null safe.
    public boolean areEqual(@Nullable ClayLaser t1, @Nullable ClayLaser t2) {
        return equalsDelegate.areEqual(t1, t2);
    }
}
