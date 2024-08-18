package com.github.trc.clayium.recipe

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.Bootstrap
import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.metatileentity.AutoIoHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.ClayTiers
import com.github.trc.clayium.common.recipe.builder.SimpleRecipeBuilder
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
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
//        every { mockWorld.isRemote } answers { false }
        every { mockWorld.notifyBlockUpdate(any(), any(), any(), any()) } just Runs
        every { mockWorld.markChunkDirty(any(), any()) } just Runs

        dummyMte = object : MetaTileEntity(clayiumId("test_metaTileEntity"), ClayTiers.CLAY,  listOf(), listOf(), "") {
            override val importItems: IItemHandlerModifiable = ItemStackHandler(1)
            override val exportItems: IItemHandlerModifiable = ItemStackHandler(1)
            override val itemInventory: IItemHandler = ItemStackHandler(1)
            val autoIoHandler: AutoIoHandler = AutoIoHandler.Importer(this)

            override fun createMetaTileEntity() = this
            override fun registerItemModel(item: Item, meta: Int) {}
            override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager) = ModularPanel.defaultPanel("test_panel")
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
            CEtFactor(ClayEnergy.of(1))
            duration(1)
        }
        testRecipeRegistry.register {
            input(ItemStack(Items.CLAY_BALL))
            output(ItemStack(Blocks.CLAY))
            CEtFactor(ClayEnergy.ZERO)
            duration(1)
        }
    }

    "recipe shouldn't be set with empty input" {
        logic.update()
        dummyMte.holder?.world?.isRemote shouldBe false
        logic.currentProgress shouldBe 0
    }
    "recipe shouldn't be set with invalid input" {
        dummyMte.importItems.setStackInSlot(0, ItemStack(Blocks.BARRIER))
        logic.update()
        logic.currentProgress shouldBe 0
    }
    "recipe shouldn't be set without enough energy" {
        dummyMte.importItems.setStackInSlot(0, ItemStack(Blocks.STONE))
        logic.update()
        logic.currentProgress shouldBe 0
    }
    "recipe should be set with valid input and enough energy" {
        dummyMte.importItems.setStackInSlot(0, ItemStack(Items.CLAY_BALL))
        logic.update()
        logic.currentProgress shouldBe 1
    }
})