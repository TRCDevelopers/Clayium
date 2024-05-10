package com.github.trcdevelopers.clayium.recipe

import com.github.trcdevelopers.clayium.Bootstrap
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.builder.SimpleRecipeBuilder
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import io.kotest.core.spec.style.StringSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

//todo: minecraft bootstrap
class TestAbstractRecipeLogic : StringSpec({

    lateinit var mockWorld: World
    lateinit var dummyMte: MetaTileEntity
    lateinit var holder: MetaTileEntityHolder
    lateinit var testRecipeRegistry: RecipeRegistry<*>
    lateinit var logic: AbstractRecipeLogic

    beforeTest {
        Bootstrap.perform()
        // todo: move to somewhere
        mockWorld = mockk()
//        every { mockWorld.isRemote } returns false
        every { mockWorld.notifyBlockUpdate(any(), any(), any(), any()) } just Runs
        every { mockWorld.markChunkDirty(any(), any()) } just Runs

        dummyMte = object : MetaTileEntity(clayiumId("test_metaTileEntity"), 1,  listOf(), listOf(), "") {
            override val importItems: IItemHandlerModifiable = ItemStackHandler(1)
            override val exportItems: IItemHandlerModifiable = ItemStackHandler(1)
            override val itemInventory: IItemHandler = ItemStackHandler(1)
            override val autoIoHandler: AutoIoHandler = AutoIoHandler.Importer(this)

            override fun createMetaTileEntity() = this
            override fun registerItemModel(item: Item, meta: Int) {}
            override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager) = null
        }
        holder = MetaTileEntityHolder().apply {
            setMetaTileEntity(dummyMte)
            world = mockWorld
        }

        testRecipeRegistry = RecipeRegistry("test_logic", SimpleRecipeBuilder(), 1, 1)

        logic = object : AbstractRecipeLogic(dummyMte, testRecipeRegistry) {
            override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
                return ce == ClayEnergy.ZERO
            }
        }

        // add some test recipes
        testRecipeRegistry.register {
            input(ItemStack(Blocks.STONE))
            output(ItemStack(Items.STICK))
            cePerTick(ClayEnergy.of(1))
            duration(1)
        }
        testRecipeRegistry.register {
            input(ItemStack(Items.CLAY_BALL))
            output(ItemStack(Blocks.CLAY))
            cePerTick(ClayEnergy.ZERO)
            duration(1)
        }
    }

    "fails with empty input" {
        logic.update()
    }
    "fails with invalid input" {
        TODO()
    }
})