package com.github.trcdevelopers.clayium.api.capability

import net.minecraft.item.ItemStack
import java.util.function.Predicate

interface IItemFilter : Predicate<ItemStack>