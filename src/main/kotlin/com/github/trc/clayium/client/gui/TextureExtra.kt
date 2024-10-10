package com.github.trc.clayium.client.gui

import codechicken.lib.colour.ColourARGB
import codechicken.lib.colour.ColourRGBA
import com.github.trc.clayium.api.extensions.ccl.*
import com.github.trc.clayium.api.util.CLog
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
    private val colors: List<ColourRGBA>?,
) : TextureAtlasSprite(id) {

    override fun getDependencies(): Collection<ResourceLocation?> {
        return extras.map { clayiumId("blocks/$it") }
    }

    override fun hasCustomLoader(manager: IResourceManager, location: ResourceLocation): Boolean {
        return true
    }

    override fun load(manager: IResourceManager, location: ResourceLocation, textureGetter: Function<ResourceLocation, TextureAtlasSprite?>): Boolean {
        val mipmapLevels = Minecraft.getMinecraft().gameSettings.mipmapLevels
        var bufImage: BufferedImage? = null
        val baseSprite = textureGetter.apply(clayiumId("blocks/${extras[0]}"))
        if (baseSprite == null) {
            CLog.error("MetalBlock: baseSprite is null")
            throw NullPointerException("baseSprite is null")
        }
        val width = baseSprite.iconWidth
        val height = baseSprite.iconHeight
        iconWidth = width
        iconHeight = height
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
        bufImage!!.getRGB(0, 0, width, height, pixels[0], 0, width)
        this.clearFramesTextureData()
        this.framesTextureData.add(pixels)
        return false
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
    for (x in 0..<image0.width) {
        for (y in 0..<image0.height) {
            val (a0, r0, g0, b0) = ColourARGB(image0.getRGB(x, y)).packIntArray()
            val (a1, r1, g1, b1) = ColourARGB(image1.getRGB(x, y)).packIntArray()

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

private fun recolorImage(image0: BufferedImage, colorRGBA: ColourRGBA): BufferedImage {
    val ret = image0
    for (x in 0..<image0.width) {
        for (y in 0..<image0.height) {
            val (a0, r0, g0, b0) = ColourARGB(image0.getRGB(x, y)).packIntArray()
            val (r1, g1, b1, a1) = colorRGBA.packIntArray()

            val a2 = a0 * a1 / 255
            val r2 = r0 * r1 / 255
            val g2 = g0 * g1 / 255
            val b2 = b0 * b1 / 255
            ret.setRGB(x, y, (r2 shl 16) + (g2 shl 8) + b2 + (a2 shl 24))
        }
    }
    return ret
}