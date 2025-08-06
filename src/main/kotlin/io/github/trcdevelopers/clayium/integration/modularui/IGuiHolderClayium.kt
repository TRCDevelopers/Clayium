package io.github.trcdevelopers.clayium.integration.modularui

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.GuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.screen.UISettings
import com.cleanroommc.modularui.value.sync.PanelSyncManager

/**
 * wraps [IGuiHolder] to absorb changes of MUI API.
 */
interface IGuiHolderClayium<T: GuiData> : IGuiHolder<T> {
    fun buildUI(data: T, syncManager: PanelSyncManager): ModularPanel

    override fun buildUI(data: T, syncManager: PanelSyncManager, settings: UISettings): ModularPanel {
        return buildUI(data, syncManager)
    }
}