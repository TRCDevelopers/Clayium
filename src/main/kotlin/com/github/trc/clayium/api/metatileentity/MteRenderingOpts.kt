package com.github.trc.clayium.api.metatileentity

import net.minecraft.util.ResourceLocation

@ConsistentCopyVisibility
data class MteRenderingOpts private constructor(
    val faceTexture: ResourceLocation?,
    val requiredTextures: List<ResourceLocation>,
    val useFaceForAllSides: Boolean,
) {
    companion object {
        fun builder()  = MteRenderingOptionsBuilder()
    }

    class MteRenderingOptionsBuilder {
        private var faceTexture: ResourceLocation? = null
        private val requiredTextures = mutableListOf<ResourceLocation>()
        private var hasFrontFacing: Boolean = true
        private var useFaceForAllSides: Boolean = false

        /**
         * Sets the facing texture for the MTE.
         * Given resource location will automatically be loaded (added to the required textures).
         */
        fun face(texture: ResourceLocation) = apply {
            this.faceTexture = texture
            this.requiredTextures.add(texture)
        }
        fun addRequiredTextures(texture: ResourceLocation) = apply { this.requiredTextures.add(texture) }
        fun noFrontFacing() = apply { this.hasFrontFacing = false }
        fun useFaceForAllSides() = apply { this.useFaceForAllSides = true }

        fun build(): MteRenderingOpts {
            // `hasFrontFacing` is redundant because `faceTexture` can be null,
            // but it is kept for clarity and to ensure that the user explicitly sets it.
            if (this.hasFrontFacing && this.faceTexture == null) {
                throw IllegalStateException("Face texture must be set if hasFrontFacing is true")
            }

            return MteRenderingOpts(
                faceTexture,
                requiredTextures,
                useFaceForAllSides
            )
        }

    }
}

