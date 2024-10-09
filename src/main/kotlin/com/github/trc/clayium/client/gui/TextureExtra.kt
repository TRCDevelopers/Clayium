package com.github.trc.clayium.client.gui

import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage
import java.util.function.Function

private const val BASE_PATH = "textures/blocks"

class TextureExtra(
    id: String,
    private val extras: List<String>,
    private val colors: IntArray?,
) : TextureAtlasSprite(id) {
    override fun hasCustomLoader(manager: IResourceManager, location: ResourceLocation): Boolean {
        return true
    }

    override fun load(manager: IResourceManager, location: ResourceLocation, textureGetter: Function<ResourceLocation?, TextureAtlasSprite?>): Boolean {
        val mipmapLevels = Minecraft.getMinecraft().gameSettings.mipmapLevels
        var bufImage: BufferedImage? = null
        val baseSprite = textureGetter.apply(createRl(extras[0], 0))!!
        val width = baseSprite.iconWidth
        val height = baseSprite.iconHeight
        val pixels = Array<IntArray?> (mipmapLevels + 1) { null }
        pixels[0] = IntArray(width * height)

        for ((i, extraTex) in extras.withIndex()) {
            val rl = createRl(extraTex, 0)
            val resource: IResource = manager.getResource(rl)

            val image = resource.inputStream.use { stream ->
                val image: BufferedImage = javax.imageio.ImageIO.read(stream)
                if (colors != null) {
                    recolorImage(image, colors[i])
                } else {
                    image
                }
            }

            bufImage = if (bufImage == null) {
                image
            } else {
                blendImages(bufImage, image)
            }
        }
        pixels[0] = bufImage!!.getRGB(0, 0, width, height, pixels[0], 0, width)
        this.clearFramesTextureData()
        this.framesTextureData.add(pixels)
        return true
    }
}

private fun createRl(path: String, mipmapLevel: Int): ResourceLocation {
    return if (mipmapLevel == 0) {
        clayiumId("$BASE_PATH/$path.png")
    } else {
        clayiumId("$BASE_PATH/mipmaps/$path.$mipmapLevel.png")
    }
}

private fun blendImages(image0: BufferedImage, image1: BufferedImage): BufferedImage {
    val ret = image0
    for (x in 0 until image0.width) {
        for (y in 0 until image0.height) {
            val (r0, g0, b0, a0) = destructRgba(image0.getRGB(x, y))
            val (r1, g1, b1, a1) = destructRgba(image1.getRGB(x, y))

            val a2 = a0 + a1 - a0 * a1 / 255
            val r2 =
                if ((a2 == 0)) r0 else (a0 * (255 - a1) * r0 / (255 * a0 + 255 * a1 - a0 * a1) + a1 * 255 * r1 / (255 * a0 + 255 * a1 - a0 * a1))
            val g2 =
                if ((a2 == 0)) g0 else (a0 * (255 - a1) * g0 / (255 * a0 + 255 * a1 - a0 * a1) + a1 * 255 * g1 / (255 * a0 + 255 * a1 - a0 * a1))
            val b2 =
                if ((a2 == 0)) b0 else (a0 * (255 - a1) * b0 / (255 * a0 + 255 * a1 - a0 * a1) + a1 * 255 * b1 / (255 * a0 + 255 * a1 - a0 * a1))
            image0.setRGB(x, y, (r2 shl 16) + (g2 shl 8) + b2 + (a2 shl 24))
        }
    }
    return ret
}

private fun recolorImage(image0: BufferedImage, color: Int): BufferedImage {
    val ret = image0
    for (x in 0 until image0.width) {
        for (y in 0 until image0.height) {
            val (r0, g0, b0, a0) = destructRgba(image0.getRGB(x, y))
            val (r1, g1, b1, a1) = destructRgba(color)

            val a2 = a0 * a1 / 255
            val r2 = r0 * r1 / 255
            val g2 = g0 * g1 / 255
            val b2 = b0 * b1 / 255
            ret.setRGB(x, y, (r2 shl 16) + (g2 shl 8) + b2 + (a2 shl 24))
        }
    }
    return ret
}

/**
 * [R, G, B, A] IntArray
 */
private fun destructRgba(color: Int): IntArray {
    return intArrayOf(color shr 16 and 0xFF, color shr 8 and 0xFF, color and 0xFF, color shr 24 and 0xFF)
}
