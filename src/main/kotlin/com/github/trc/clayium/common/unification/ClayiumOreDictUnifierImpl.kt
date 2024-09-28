package com.github.trc.clayium.common.unification

import com.github.trc.clayium.api.W
import com.github.trc.clayium.api.unification.stack.MultiItemVariantMap
import com.github.trc.clayium.api.unification.stack.MutableItemVariantMap
import com.github.trc.clayium.api.unification.stack.SingleItemVariantMap
import com.github.trc.clayium.api.util.copyWithSize
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.OreDictionary

object ClayiumOreDictUnifierImpl : IOreDictUnifier {

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

        oreName2Stacks.computeIfAbsent(oreName) { mutableListOf() }.add(stack.copyWithSize(1))
        //todo
    }

    override fun registerOre(stack: ItemStack, oreDict: String) {
        OreDictionary.registerOre(oreDict, stack)
    }

    override fun getOreNames(stack: ItemStack): Set<String> {
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

    override fun get(oreDict: String, amount: Int): ItemStack {
        val stack =  oreName2Stacks[oreDict]?.firstOrNull()
            ?.copyWithSize(amount) ?: return ItemStack.EMPTY
        if (stack.metadata == W) stack.itemDamage = 0
        return stack
    }

    override fun getAll(oreDict: String, amount: Int): List<ItemStack> {
        return OreDictionary.getOres(oreDict).map { it.copyWithSize(amount) }
    }
}