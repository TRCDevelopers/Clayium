package com.github.trc.clayium.util

import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.util.TileEntityAccess
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TestTileEntityAccess :
    StringSpec({
        lateinit var world: World
        lateinit var tileEntity: TileEntity
        val pos = BlockPos(1, 2, 3)

        beforeTest {
            Bootstrap.perform()

            world = mockk()
            tileEntity = mockk()

            every { tileEntity.getPos() } returns pos
            every { tileEntity.isInvalid } returns false
        }

        "not null get(IfLoaded)" {
            every { world.getTileEntity(pos) } returns tileEntity
            every { world.isBlockLoaded(pos) } returns true

            val access = TileEntityAccess(world, pos)
            access.get() shouldBe tileEntity
            access.getIfLoaded() shouldBe tileEntity

            every { world.isBlockLoaded(pos) } returns false
            access.getIfLoaded() shouldBe null
            every { tileEntity.isInvalid } returns true
            every { world.getTileEntity(pos) } returns null
            access.get() shouldBe null
        }
    })
