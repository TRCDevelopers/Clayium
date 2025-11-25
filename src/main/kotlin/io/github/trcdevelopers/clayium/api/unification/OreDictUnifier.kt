package io.github.trcdevelopers.clayium.api.unification

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.W
import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.MultiItemVariantMap
import io.github.trcdevelopers.clayium.api.unification.stack.MutableItemVariantMap
import io.github.trcdevelopers.clayium.api.unification.stack.SingleItemVariantMap
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {
    private val item2OreNames = mutableMapOf<Item, MutableItemVariantMap<MutableSet<String>>>()
    private val oreName2Stacks = mutableMapOf<String, MutableList<ItemStack>>()

    init {
        OreDictionary.getOreNames().forEach { oreName ->
            OreDictionary.getOres(oreName).forEach { stack ->
                onOreRegistration(OreDictionary.OreRegisterEvent(oreName, stack))
            }
        }
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onOreRegistration(e: OreDictionary.OreRegisterEvent) {
        val oreName = e.name
        val stack = e.ore
        val item = stack.item
        val meta = stack.itemDamage

        val variantMap = item2OreNames.computeIfAbsent(item) {
            if (stack.hasSubtypes) MultiItemVariantMap() else SingleItemVariantMap()
        }
        val names = variantMap.computeIfAbsent(meta.toShort()) { mutableSetOf() }
        names.add(oreName)

        oreName2Stacks.computeIfAbsent(oreName) { mutableListOf() }.apply {
            if (stack.item.registryName?.namespace == MOD_ID) {
                add(0, stack.copyWithSize(1))
            } else {
                add(stack.copyWithSize(1))
            }
        }
    }

    fun registerOre(stack: ItemStack, oreDict: String) {
        OreDictionary.registerOre(oreDict, stack)
    }

    fun registerOre(stack: ItemStack, orePrefix: OrePrefix, material: IMaterial)  {
        this.registerOre(stack, UnificationEntry(orePrefix, material).toString())
    }

    fun getOreNames(stack: ItemStack): Set<String> {
        val item = stack.item
        val meta = stack.itemDamage
        val variantMap = item2OreNames[item]
            ?: return emptySet()
        val names = variantMap[meta.toShort()]
        if (meta == W) {
            return names ?: emptySet()
        }

        val wildcardNames = variantMap[W.toShort()]
            ?: emptySet()

        return names?.union(wildcardNames) ?: wildcardNames
    }

    fun get(oreDict: String, amount: Int = 1): ItemStack {
        val stack =  oreName2Stacks[oreDict]?.firstOrNull()
            ?.copyWithSize(amount) ?: return ItemStack.EMPTY
        if (stack.metadata == W) stack.itemDamage = 0
        return stack
    }

    fun get(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = get(UnificationEntry(orePrefix, material).toString(), amount)

    fun getAll(oreDict: String, amount: Int = 1): List<ItemStack> {
        return OreDictionary.getOres(oreDict).map { it.copyWithSize(amount) }
    }
    fun getAll(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = getAll(UnificationEntry(orePrefix, material), amount)
    fun getAll(oreDict: UnificationEntry, amount: Int = 1) = getAll(oreDict.toString(), amount)

    fun has(stack: ItemStack, oreDict: String): Boolean {
        val item = stack.item
        val meta = stack.itemDamage
        val variantMap = item2OreNames[item] ?: return false

        val damage = meta.toShort()
        val names = variantMap[damage]
        if (names != null && names.contains(oreDict)) return true

        if (meta == W) return false

        val wildcardNames = variantMap[W.toShort()]
        return wildcardNames != null && wildcardNames.contains(oreDict)
    }

    fun exists(oreDict: String): Boolean = !get(oreDict).isEmpty
    fun exists(orePrefix: OrePrefix, material: IMaterial): Boolean = exists(UnificationEntry(orePrefix, material).toString())

    fun registerAdditionalOreDict(oreDict: String, additionalOreDict: String) {
        val stack = this.get(oreDict)
        if (stack.isEmpty) {
            CLog.error("Ore {} is empty. You should register it before using this method." , oreDict)
        }
        this.registerOre(stack, additionalOreDict)
    }

    fun registerAdditionalOreDict(orePrefix: OrePrefix, material: IMaterial, additionalOreDict: String) {
        this.registerAdditionalOreDict(UnificationEntry(orePrefix, material).toString(), additionalOreDict)
    }
}