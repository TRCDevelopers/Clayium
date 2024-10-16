package com.github.trc.clayium.common.recipe.builder

import com.cleanroommc.groovyscript.api.IIngredient
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.IMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.items.metaitem.MetaItemClayium
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.chanced.ChancedOutput
import com.github.trc.clayium.common.recipe.chanced.ChancedOutputList
import com.github.trc.clayium.common.recipe.chanced.IChancedOutputLogic
import com.github.trc.clayium.common.recipe.ingredient.CItemRecipeInput
import com.github.trc.clayium.common.recipe.ingredient.CMultiOreRecipeInput
import com.github.trc.clayium.common.recipe.ingredient.COreRecipeInput
import com.github.trc.clayium.common.recipe.ingredient.CRecipeInput
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Optional
import kotlin.math.pow

@Suppress("UNCHECKED_CAST", "FunctionName")
abstract class RecipeBuilder<R: RecipeBuilder<R>>(
    protected val inputs: MutableList<CRecipeInput>,
    protected val outputs: MutableList<ItemStack>,
    protected val chancedOutputs: MutableList<ChancedOutput<ItemStack>>,
    protected var chancedOutputLogic: IChancedOutputLogic?,
    protected var duration: Long,
    protected var cePerTick: ClayEnergy,
    protected var tier: Int,
) {
    constructor() : this(mutableListOf(), mutableListOf(), mutableListOf(), null, 0, ClayEnergy.ZERO, 0)

    constructor(another: RecipeBuilder<R>) : this(another.inputs.toMutableList(), another.outputs.toMutableList(),
        another.chancedOutputs, another.chancedOutputLogic,
        another.duration, another.cePerTick, another.tier) {
        recipeRegistry = another.recipeRegistry
    }

    protected lateinit var recipeRegistry: RecipeRegistry<R>

    abstract fun copy(): R

    fun setRegistry(registry: RecipeRegistry<R>): R {
        recipeRegistry = registry
        return this as R
    }

    fun inputs(vararg inputsIn: CRecipeInput): R {
        inputsIn.forEach { input ->
            if (input.amount <= 0) {
                CLog.error("input amount must be greater than 0")
                return@forEach
            }
            inputs.add(input)
        }
        return this as R
    }

    fun input(stack: ItemStack) = inputs(CItemRecipeInput(listOf(stack), stack.count))
    fun input(item: Item, amount: Int = 1) = input(ItemStack(item, amount))
    fun input(metaItem: MetaItemClayium.MetaValueItem, amount: Int = 1) = input(metaItem.getStackForm(amount))
    fun input(metaTileEntity: MetaTileEntity, amount: Int = 1) = input(metaTileEntity.getStackForm(amount))
    fun input(block: Block, amount: Int = 1) = input(ItemStack(block, amount))
    fun input(oreDict: String, amount: Int = 1) = inputs(COreRecipeInput(oreDict, amount))
    open fun input(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = inputs(COreRecipeInput(UnificationEntry(orePrefix, material).toString(), amount))
    fun input(prefixes: Array<OrePrefix>, material: IMaterial, amount: Int = 1): R {
        val entries = prefixes.map { UnificationEntry(it, material) }.toTypedArray()
        return inputs(CMultiOreRecipeInput(amount, *entries))
    }

    fun notConsumable(stack: ItemStack) = inputs(CItemRecipeInput(stack, stack.count, isConsumable = false))
    fun notConsumable(item: Item, amount: Int = 1) = notConsumable(ItemStack(item, amount))
    fun notConsumable(metaItem: MetaItemClayium.MetaValueItem, amount: Int = 1) = notConsumable(metaItem.getStackForm(amount))
    fun notConsumable(metaTileEntity: MetaTileEntity, amount: Int = 1) = notConsumable(metaTileEntity.getStackForm(amount))
    fun notConsumable(block: Block, amount: Int = 1) = notConsumable(ItemStack(block, amount))
    fun notConsumable(oreDict: String, amount: Int = 1) = inputs(COreRecipeInput(oreDict, amount, isConsumable = false))
    fun notConsumable(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = notConsumable(UnificationEntry(orePrefix, material).toString(), amount)

    fun outputs(vararg stacks: ItemStack): R {
        outputs.addAll(stacks)
        return this as R
    }

    fun output(stack: ItemStack) = outputs(stack)
    fun output(item: Item, amount: Int = 1) = output(ItemStack(item, amount))
    fun output(metaItem: MetaItemClayium.MetaValueItem, amount: Int = 1) = output(metaItem.getStackForm(amount))
    fun output(metaTileEntity: MetaTileEntity, amount: Int = 1) = output(metaTileEntity.getStackForm(amount))
    fun output(block: Block, amount: Int = 1) = output(ItemStack(block, amount))
    fun output(oreDict: String, amount: Int = 1) = outputs(OreDictUnifier.get(oreDict, amount))
    fun output(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = outputs(OreDictUnifier.get(orePrefix, material, amount))

    fun chancedOutput(output: ItemStack, chance: Int): R {
        chancedOutputs.add(ChancedOutput(output, chance))
        return this as R
    }
    fun chancedOutput(item: Item, amount: Int, chance: Int): R = chancedOutput(ItemStack(item, amount), chance)
    fun chancedOutput(item: Item, chance: Int): R = chancedOutput(item, 1, chance)
    fun chancedOutput(metaItem: MetaItemClayium.MetaValueItem, amount: Int, chance: Int): R = chancedOutput(metaItem.getStackForm(amount), chance)
    fun chancedOutput(metaTileEntity: MetaTileEntity, amount: Int, chance: Int): R = chancedOutput(metaTileEntity.getStackForm(amount), chance)
    fun chancedOutput(block: Block, amount: Int, chance: Int): R = chancedOutput(ItemStack(block, amount), chance)
    fun chancedOutput(block: Block, chance: Int): R = chancedOutput(block, 1, chance)
    fun chancedOutput(oreDict: String, amount: Int, chance: Int): R = chancedOutput(OreDictUnifier.get(oreDict, amount), chance)
    fun chancedOutput(oreDict: String, chance: Int) = chancedOutput(oreDict, 1, chance)
    fun chancedOutput(orePrefix: OrePrefix, material: IMaterial, amount: Int, chance: Int): R = chancedOutput(OreDictUnifier.get(orePrefix, material, amount), chance)
    fun chancedOutput(orePrefix: OrePrefix, material: IMaterial, chance: Int) = chancedOutput(orePrefix, material, 1, chance)

    fun chancedLogic(logic: IChancedOutputLogic): R {
        chancedOutputLogic = logic
        return this as R
    }

    fun duration(duration: Int) = duration(duration.toLong())
    fun duration(duration: Long): R {
        this.duration = duration
        return this as R
    }

    @JvmName("CEtRaw")
    fun CEt(cePerTick: ClayEnergy): R {
        this.cePerTick = cePerTick
        return this as R
    }

    /**
     * | Tier | CEt |
     * |:----:|:---:|
     * | 0   | 10u   |
     * | 1 | 10u |
     * | 2 | 10u |
     * | 3 | 100u |
     * | 4 | 1m |
     * | 5 | 10m |
     * | 6 | 100m |
     * | 7 | 1 |
     * | 8 | 10 |
     * | 9 | 100 |
     * | 10 | 1k |
     * | 11 | 10k |
     * | 12 | 100k |
     * | 13 | 1M |
     */
    fun CEtByTier(tier: Int = this.tier): R {
        return this.CEt(1.0, tier)
    }

    fun CEtFactor(factor: Double): R {
        return this.CEt(factor, this.tier)
    }

    fun CEt(factor: Double, tier: Int): R{
        return this.CEt(ClayEnergy((factor * 100.0 * 10.0.pow(tier - 4.0)).toLong().coerceAtLeast(1)))
    }

    fun tier(tier: Int): R {
        this.tier = tier
        return this as R
    }

    fun defaultCEt(): R {
        return this.CEtByTier(tier = this.tier)
    }

    /* Grs */
    @Optional.Method(modid = Mods.Names.GROOVY_SCRIPT)
    fun input(input: IIngredient) = this.inputs(CItemRecipeInput(input.matchingStacks.toList(), input.amount))
    @Optional.Method(modid = Mods.Names.GROOVY_SCRIPT)
    fun output(output: IIngredient) = this.output(output.matchingStacks.firstOrNull() ?: ItemStack.EMPTY)
    // not optional for java interop
    fun CEt(cePerTick: Long) = this.CEt(ClayEnergy.of(cePerTick))
    fun CEtMilli(cePerTick: Int) = this.CEt(ClayEnergy.milli(cePerTick.toLong()))
    fun CEtMicro(cePerTick: Int) = this.CEt(ClayEnergy.micro(cePerTick.toLong()))

    open fun buildAndRegister() {
        recipeRegistry.addRecipe(build())
    }

    open fun build(): Recipe {
        setDefaults()

        val chancedOutputList = if (chancedOutputLogic != null) {
            ChancedOutputList(chancedOutputs, chancedOutputLogic!!)
        } else {
            null
        }

        return Recipe(inputs, outputs, chancedOutputList, duration, cePerTick, tier)
    }

    protected fun setDefaults() {
        if (this.cePerTick.energy == 0L) {
            this.CEt(tier = this.tier, factor = 1.0)
        }
    }
}