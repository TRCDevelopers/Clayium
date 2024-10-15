package com.github.trc.clayium.common.gui.sync

import com.cleanroommc.modularui.utils.serialization.IByteBufSerializer
import com.cleanroommc.modularui.utils.serialization.IEquals
import com.cleanroommc.modularui.value.sync.GenericSyncValue
import net.minecraft.network.PacketBuffer
import net.minecraft.village.MerchantRecipeList
import java.util.function.Consumer
import java.util.function.Supplier

class MerchantRecipeListSyncValue(
    getter: Supplier<MerchantRecipeList>,
    setter: Consumer<MerchantRecipeList>,
) : GenericSyncValue<MerchantRecipeList>(
    getter, setter, MerchantRecipeList::readFromBuf, MerchantRecipeListSerializer, equals,
)

private object MerchantRecipeListSerializer : IByteBufSerializer<MerchantRecipeList> {
    override fun serialize(buffer: PacketBuffer, value: MerchantRecipeList) {
        value.writeToBuf(buffer)
    }
}

private val equals = IEquals.wrapNullSafe(MerchantRecipeEquals())

private class MerchantRecipeEquals : IEquals<MerchantRecipeList> {
    override fun areEqual(t1: MerchantRecipeList, t2: MerchantRecipeList): Boolean {
        val t1Tags = t1.map { it.writeToTags() }
        val t2Tags = t2.map { it.writeToTags() }

        return t1Tags.zip(t2Tags).all { (t1, t2) -> t1 == t2 }
    }
}