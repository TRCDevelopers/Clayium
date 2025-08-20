package io.github.trcdevelopers.clayium.integration.jei.transfer

import io.github.trcdevelopers.clayium.common.gui.ContainerClayCraftingBoard
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo
import net.minecraft.inventory.Slot

object CcbRecipeTransferInfo : IRecipeTransferInfo<ContainerClayCraftingBoard> {
    override fun getContainerClass(): Class<ContainerClayCraftingBoard> {
        return ContainerClayCraftingBoard::class.java
    }

    override fun getRecipeCategoryUid(): String {
        return VanillaRecipeCategoryUid.CRAFTING
    }

    override fun canHandle(container: ContainerClayCraftingBoard): Boolean {
        return true
    }

    override fun getRecipeSlots(container: ContainerClayCraftingBoard): List<Slot> {
        return container.inventorySlots.slice(1..9)
    }

    override fun getInventorySlots(container: ContainerClayCraftingBoard): List<Slot> {
        return container.inventorySlots.slice(10..<container.inventorySlots.size)
    }
}
