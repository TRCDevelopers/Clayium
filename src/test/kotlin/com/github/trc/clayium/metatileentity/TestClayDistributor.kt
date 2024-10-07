package com.github.trc.clayium.metatileentity

import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.metatileentities.DistributorMetaTileEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class TestClayDistributor : StringSpec({

    beforeTest {
        Bootstrap.perform()
    }

    "Test Distribute Items" {
        val mte = DistributorMetaTileEntity(clayiumId("distributor"), ClayTiers.CLAY_STEEL)
        val distributor = mte.ioHandler
        val handler0 = ItemStackHandler(2)
        val handler1 = ItemStackHandler(2)

        val map = mutableMapOf<EnumFacing, IItemHandler>().apply {
            put(EnumFacing.NORTH, handler0)
            put(EnumFacing.SOUTH, handler1)
        }

        val source = ItemStackHandler(2).apply {
            insertItem(0, ItemStack(Items.CLAY_BALL, 64), false)
            insertItem(1, ItemStack(Items.DIAMOND, 64), false)
        }

        distributor.distribute(source, map)

        val stack00 = handler0.getStackInSlot(0)
        stack00.item shouldBe Items.CLAY_BALL
        stack00.count shouldBe 32
        val stack01 = handler0.getStackInSlot(1)
        stack01.item shouldBe Items.DIAMOND
        stack01.count shouldBe 32

        val stack10 = handler1.getStackInSlot(0)
        stack10.item shouldBe Items.CLAY_BALL
        stack10.count shouldBe 32
        val stack11 = handler1.getStackInSlot(1)
        stack11.item shouldBe Items.DIAMOND
        stack11.count shouldBe 32
    }
})