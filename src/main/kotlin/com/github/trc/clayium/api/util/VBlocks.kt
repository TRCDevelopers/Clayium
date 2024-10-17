package com.github.trc.clayium.api.util

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

/** vanilla blocks that has metadata. these blocks are not in [Blocks] class. */
object VBlocks {
    val PODZOL = ItemStack(Blocks.DIRT, 1, 2)

    val DANDELION = ItemStack(Blocks.YELLOW_FLOWER)
    val POPPY = ItemStack(Blocks.RED_FLOWER)
    val BLUE_ORCHID = ItemStack(Blocks.RED_FLOWER, 1, 1)
    val ALLIUM = ItemStack(Blocks.RED_FLOWER, 1, 2)
    val AZURE_BLUET = ItemStack(Blocks.RED_FLOWER, 1, 3)
    val RED_TULIP = ItemStack(Blocks.RED_FLOWER, 1, 4)
    val ORANGE_TULIP = ItemStack(Blocks.RED_FLOWER, 1, 5)
    val WHITE_TULIP = ItemStack(Blocks.RED_FLOWER, 1, 6)
    val PINK_TULIP = ItemStack(Blocks.RED_FLOWER, 1, 7)
    val OXEYE_DAISY = ItemStack(Blocks.RED_FLOWER, 1, 8)
    val SUNFLOWER = ItemStack(Blocks.DOUBLE_PLANT, 1, 0)
    val LILAC = ItemStack(Blocks.DOUBLE_PLANT, 1, 1)
    val TALL_GRASS = ItemStack(Blocks.DOUBLE_PLANT, 1, 2)
    val LARGE_FERN = ItemStack(Blocks.DOUBLE_PLANT, 1, 3)
    val ROSE_BUSH = ItemStack(Blocks.DOUBLE_PLANT, 1, 4)
    val PEONY = ItemStack(Blocks.DOUBLE_PLANT, 1, 5)
}
