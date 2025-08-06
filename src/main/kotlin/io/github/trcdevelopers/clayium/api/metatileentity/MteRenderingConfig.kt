package io.github.trcdevelopers.clayium.api.metatileentity

import net.minecraft.tileentity.TileEntityBeacon
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import java.util.function.Supplier

data class MteRenderingConfig private constructor(
    val faceTextureSupplier: Supplier<ResourceLocation?>,
    val requiredTextures: List<ResourceLocation>,
    val useFaceForAllSides: Boolean,
    val renderPass: Int,
    val maxRenderDistanceSquared: Double,
    val renderBoundingBox: AxisAlignedBB?,
    val useGlobalRenderer: Boolean,
) {
    val faceTexture get() = faceTextureSupplier.get()

    companion object {
        fun face(texture: ResourceLocation) = builder().face(texture).build()
        fun noFace() = builder().noFrontFacing().build()
        fun builder()  = MteRenderingConfigBuilder()
    }

    class MteRenderingConfigBuilder {
        private var faceSupplier: Supplier<ResourceLocation?>? = null
        private val requiredTextures = mutableListOf<ResourceLocation>()
        private var hasFrontFacing: Boolean = true
        private var useFaceForAllSides: Boolean = false

        private var renderPass: Int = 0
        private var maxRenderDistanceSquared: Double = 4096.0
        private var renderBoundingBox: AxisAlignedBB? = null
        private var useGlobalRenderer: Boolean = false

        /**
         * Sets the facing texture for the MTE.
         * Given resource location will automatically be loaded (added to the required textures).
         */
        fun face(texture: ResourceLocation) = apply {
            this.faceSupplier = Supplier { texture }
            this.requiredTextures.add(texture)
        }

        /**
         * Sets a dynamic supplier for the facing texture.
         * You have to add the requiredTextures manually.
         */
        fun dynFace(supplier: Supplier<ResourceLocation?>) = apply {
            this.faceSupplier = supplier
        }
        fun addRequiredTextures(vararg textures: ResourceLocation) = apply { this.requiredTextures.addAll(textures) }
        fun noFrontFacing() = apply { this.hasFrontFacing = false }
        fun useFaceForAllSides() = apply { this.useFaceForAllSides = true }

        /**
         * Utility method to set the following:
         * - `maxRenderDistanceSquared` to `Double.POSITIVE_INFINITY`
         * - `renderBoundingBox` to `TileEntityBeacon.INFINITE_EXTENT_AABB`
         * - `useGlobalRenderer` to `true`
         */
        fun alwaysRender() = apply {
            this.maxRenderDistanceSquared = Double.POSITIVE_INFINITY
            this.renderBoundingBox = TileEntityBeacon.INFINITE_EXTENT_AABB
            this.useGlobalRenderer = true
        }

        /**
         * default: 0
         */
        fun renderPass(pass: Int) = apply {
            this.renderPass = pass
        }

        /**
         * default: 4096.0
         */
        fun maxRenderDistanceSquared(distance: Double) = apply {
            if (distance < 0) {
                throw IllegalArgumentException("Max render distance squared must be non-negative")
            }
            this.maxRenderDistanceSquared = distance
        }

        /**
         * null for using TileEntity's default bounding box.
         * Default: null
         */
        fun renderBoundingBox(boundingBox: AxisAlignedBB?) = apply {
            this.renderBoundingBox = boundingBox
        }

        /**
         * Default: false
         */
        fun useGlobalRenderer() = apply {
            this.useGlobalRenderer = true
        }

        fun build(): MteRenderingConfig {
            // `hasFrontFacing` is redundant because `faceSupplier` can return null,
            // but it is kept for clarity and to ensure that the user explicitly sets it.
            if (this.hasFrontFacing && this.faceSupplier == null) {
                throw IllegalStateException("Face texture must be set if hasFrontFacing is true")
            }

            return MteRenderingConfig(
                faceSupplier ?: Supplier { null },
                requiredTextures,
                useFaceForAllSides,
                renderPass,
                maxRenderDistanceSquared,
                renderBoundingBox,
                useGlobalRenderer,
            )
        }

    }
}

