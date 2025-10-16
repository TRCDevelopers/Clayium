package io.github.trcdevelopers.clayium.common.recipe.builder

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import io.github.trcdevelopers.clayium.api.util.Mods
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Optional
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class MatterTransformerRecipeBuilder : RecipeBuilder<MatterTransformerRecipeBuilder> {

    private var defaultPrefix: OrePrefix? = null

    constructor() : super()
    constructor(another: MatterTransformerRecipeBuilder) : super(another)

    private val chains = mutableListOf<Any>()

    override fun copy() = MatterTransformerRecipeBuilder(this)
        .also { it.defaultPrefix = this.defaultPrefix }

    fun defaultPrefix(orePrefix: OrePrefix): MatterTransformerRecipeBuilder {
        this.defaultPrefix = orePrefix
        return this
    }

    @OptIn(ExperimentalContracts::class)
    fun verifyDefaultPrefixIsSet(defaultPrefix: OrePrefix?) {
        contract {
            returns() implies (defaultPrefix != null)
        }
        if (defaultPrefix == null) {
            throw IllegalStateException("Default OrePrefix is not set, but a material only method is called.")
        }
    }

    fun input(material: CMaterial): MatterTransformerRecipeBuilder {
        val defaultPrefix = this.defaultPrefix
        verifyDefaultPrefixIsSet(defaultPrefix)
        return this.input(defaultPrefix, material)
    }
    fun output(material: CMaterial): MatterTransformerRecipeBuilder {
        val defaultPrefix = this.defaultPrefix
        verifyDefaultPrefixIsSet(defaultPrefix)
        return this.output(defaultPrefix, material)
    }
    @Optional.Method(modid = Mods.Names.GREGTECH)
    fun input(material: GtMaterial): MatterTransformerRecipeBuilder {
        val defaultPrefix = this.defaultPrefix
        verifyDefaultPrefixIsSet(defaultPrefix)
        return this.input("${defaultPrefix.camel}${material.toCamelCaseString()}")
    }
    @Optional.Method(modid = Mods.Names.GREGTECH)
    fun output(material: GtMaterial): MatterTransformerRecipeBuilder {
        val defaultPrefix = this.defaultPrefix
        verifyDefaultPrefixIsSet(defaultPrefix)
        return this.output("${defaultPrefix.camel}${material.toCamelCaseString()}")
    }

    /**
     * Returns a new builder instance with the current output set as the input,
     * and the output set to the given ore dictionary.
     * If the given oreDict does not exist, this does nothing.
     * Also sets the CEt, duration, and tier to the current values.
     * These values can be reset by calling the respective methods.
     *
     * ```
     * registry.builder()
     *    .CEt(ClayEnergy.of(1)).duration(20).tier(7)
     *    .input("ingotIron")
     *    .output("ingotCopper")
     *    .chain("ingotGold")
     *    .chain("someInvalidOreDict")
     *    .chain("gemDiamond").tier(8).duration(200)
     *    .buildAndRegister()
     * ```
     * This will generate these recipes:
     * - `ingotIron` -> `ingotCopper`
     * - `ingotCopper` -> `ingotGold`
     * - `ingotGold` -> `gemDiamond`
     */
    fun chain(oreDict: String): MatterTransformerRecipeBuilder {
        if (OreDictUnifier.get(oreDict).isEmpty) return this
        this.buildAndRegister()
        val newBuilder = this.recipeRegistry.builder()
            .tier(this.tier).CEt(this.cePerTick).duration(this.duration)
            .input(this.outputs[0])
            .output(oreDict)
        if (defaultPrefix != null) newBuilder.defaultPrefix(defaultPrefix!!)
        return newBuilder
    }

    fun chain(orePrefix: OrePrefix, material: CMaterial) = chain(UnificationEntry(orePrefix, material).toString())
    fun chain(material: CMaterial): MatterTransformerRecipeBuilder {
        val defaultPrefix = this.defaultPrefix
        verifyDefaultPrefixIsSet(defaultPrefix)
        return chain(defaultPrefix, material)
    }
    @Optional.Method(modid = Mods.Names.GREGTECH)
    fun chain(material: GtMaterial): MatterTransformerRecipeBuilder {
        val defaultPrefix = this.defaultPrefix
        verifyDefaultPrefixIsSet(defaultPrefix)
        return chain("${defaultPrefix.camel}${material.toCamelCaseString()}")
    }

    fun chain(block: Block): MatterTransformerRecipeBuilder {
        this.buildAndRegister()
        val newBuilder = this.recipeRegistry.builder()
            .tier(this.tier).CEt(this.cePerTick).duration(this.duration)
            .input(this.outputs[0])
            .output(block)
        return newBuilder
    }

    fun chain(item: Item): MatterTransformerRecipeBuilder {
        this.buildAndRegister()
        val newBuilder = this.recipeRegistry.builder()
            .tier(this.tier).CEt(this.cePerTick).duration(this.duration)
            .input(this.outputs[0])
            .output(item)
        return newBuilder
    }

    fun chain(stack: ItemStack): MatterTransformerRecipeBuilder {
        this.buildAndRegister()
        val newBuilder = this.recipeRegistry.builder()
            .tier(this.tier).CEt(this.cePerTick).duration(this.duration)
            .input(this.outputs[0])
            .output(stack)
        return newBuilder
    }
    @Optional.Method(modid = Mods.Names.GREGTECH)
    fun chain(orePrefix: GtOrePrefix, material: GtMaterial) = chain(GtOreDictUnifier.get(orePrefix, material))

    fun chainIf(shouldChain: Boolean, oreDict: String): MatterTransformerRecipeBuilder {
        return if (shouldChain) chain(oreDict) else this
    }
    fun chainIf(shouldChain: Boolean, orePrefix: OrePrefix, material: CMaterial): MatterTransformerRecipeBuilder {
        return if (shouldChain) chain(orePrefix, material) else this
    }
    fun chainIf(shouldChain: Boolean, material: CMaterial): MatterTransformerRecipeBuilder {
        return if (shouldChain) chain(material) else this
    }
}