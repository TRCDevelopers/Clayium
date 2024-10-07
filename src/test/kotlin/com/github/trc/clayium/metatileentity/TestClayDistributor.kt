package com.github.trc.clayium.metatileentity

import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.metatileentities.DistributorMetaTileEntity
import com.github.trc.clayium.util.beEmpty
import com.github.trc.clayium.util.haveCount
import com.github.trc.clayium.util.haveItem
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class TestClayDistributor : StringSpec({

    lateinit var mte: DistributorMetaTileEntity
    lateinit var distributor: DistributorMetaTileEntity.DistributorIoHandler

    beforeTest {
        Bootstrap.perform()
        mte = DistributorMetaTileEntity(clayiumId("distributor"), ClayTiers.CLAY_STEEL)
        distributor = mte.ioHandler
    }

    "Distribute 64 items to 2 handlers" {
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

        // Clay Steel Distributor has 64/tick throughput, so DIAMOND shouldn't be transferred
        val stack0 = handler0.getStackInSlot(0)
        stack0 should haveItem(Items.CLAY_BALL)
        stack0 should haveCount(32)
        handler0.getStackInSlot(1) should beEmpty()

        val stack1 = handler1.getStackInSlot(0)
        stack1 should haveItem(Items.CLAY_BALL)
        stack1 should haveCount(32)
        handler1.getStackInSlot(1) should beEmpty()
    }

    "Distribute 11 items to 2 handlers" {
        val handler0 = ItemStackHandler(2)
        val handler1 = ItemStackHandler(2)

        val map = mutableMapOf<EnumFacing, IItemHandler>().apply {
            put(EnumFacing.NORTH, handler0)
            put(EnumFacing.SOUTH, handler1)
        }

        val source = ItemStackHandler(2).apply {
            insertItem(0, ItemStack(Items.CLAY_BALL, 3), false)
            insertItem(1, ItemStack(Items.DIAMOND, 64), false)
        }

        distributor.distribute(source, map)

        // 3 ClayBall → 2, 1
        // 61 Diamond → 31, 30
        // 2 + 1 + 31 + 30 = 64
        val stack0 = handler0.getStackInSlot(0)
        val stack1 = handler1.getStackInSlot(0)

        stack0 should haveItem(Items.CLAY_BALL)
        stack1 should haveItem(Items.CLAY_BALL)

        val counts0 = Pair(stack0.count, stack1.count)
        (counts0 == Pair(1, 2) || counts0 == Pair(2, 1)) shouldBe true

        val stack01 = handler0.getStackInSlot(1)
        val stack11 = handler1.getStackInSlot(1)

        stack01 should haveItem(Items.DIAMOND)
        stack11 should haveItem(Items.DIAMOND)

        val counts1 = Pair(stack01.count, stack11.count)
        (counts1 == Pair(30, 31) || counts1 == Pair(31, 30)) shouldBe true
    }
})