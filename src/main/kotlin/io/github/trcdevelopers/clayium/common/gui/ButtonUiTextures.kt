package io.github.trcdevelopers.clayium.common.gui

import com.cleanroommc.modularui.drawable.UITexture
import net.minecraft.util.ResourceLocation

/**
 * Utility for ModularUI.
 * This class holds 3 [UITexture]s (disabled, enabled, hovered) for a button.
 * Textures are assumed to be in a single texture file, arranged vertically in the order: disabled, enabled, hovered.
 */
class ButtonUiTextures(
    location: ResourceLocation,
    u: Int,
    v: Int,
    fileWidth: Int = 256,
    fileHeight: Int = 256,
    buttonWidth: Int = 16,
    buttonHeight: Int = 16,
) {

    val disabled: UITexture = UITexture.builder()
        .location(location)
        .imageSize(fileWidth, fileHeight)
        .xy(u, v, buttonWidth, buttonHeight)
        .build()

    val enabled: UITexture = UITexture.builder()
        .location(location)
        .imageSize(fileWidth, fileHeight)
        .xy(u, v + buttonHeight, buttonWidth, buttonHeight)
        .build()

    val hovered: UITexture = UITexture.builder()
        .location(location)
        .imageSize(fileWidth, fileHeight)
        .xy(u, v + buttonHeight * 2, buttonWidth, buttonHeight)
        .build()
}