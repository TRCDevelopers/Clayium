package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation

abstract class AbstractWorkableV2(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, "workable_v2") {
    protected var recipeOutput: IRecipeOutputs? = null

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        recipeOutput?.let { data.setTag("recipe_output", it.serializeNBT()) }
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        if (data.hasKey("recipe_output") && data.hasKey("recipe_output_type")) {
            val id = ResourceLocation(data.getString("recipe_output_type"))
            val output = CRecipes.getRecipeOutput(id)
            if (output != null) {
                output.deserializeNBT(data.getCompoundTag("recipe_output"))
                this.recipeOutput = output
            }
        }
    }
}