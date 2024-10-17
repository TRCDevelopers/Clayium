package com.github.trc.clayium.api.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import java.lang.reflect.ParameterizedType

@Suppress("OVERRIDE_DEPRECATION")
open class VariantBlock<E>(
    material: Material,
) : Block(material) where E : Enum<E>, E : IStringSerializable {

    lateinit var variantProperty: PropertyEnum<E>
        protected set

    protected lateinit var values: Array<E>

    init {
        require(values.size <= 16)
        defaultState = blockState.baseState.withProperty(variantProperty, values[0])
    }

    override fun createBlockState(): BlockStateContainer {
        val enumClass: Class<E> = getActualTypeParameter(javaClass, VariantBlock::class.java)
        variantProperty = PropertyEnum.create("variant", enumClass)
        values = enumClass.enumConstants
        return BlockStateContainer(this, variantProperty)
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (e in values) items.add(getItem(e))
    }

    override fun getMetaFromState(state: IBlockState) = state.getValue(variantProperty).ordinal

    override fun getStateFromMeta(meta: Int) =
        defaultState.withProperty(variantProperty, values[meta])

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    fun getItem(variant: E, amount: Int = 1) = ItemStack(this, amount, variant.ordinal)

    fun getEnum(state: IBlockState): E = state.getValue(variantProperty)

    fun getEnum(stack: ItemStack): E = values[stack.metadata.coerceAtMost(values.size - 1)]

    companion object {
        // copied from GTCEu
        @Suppress("UNCHECKED_CAST")
        fun <T, E> getActualTypeParameter(
            thisClass: Class<out T>,
            declaringClass: Class<T>
        ): Class<E> {
            var type = thisClass.genericSuperclass

            while (type !is ParameterizedType || type.rawType != declaringClass) {
                type =
                    if (type is ParameterizedType) {
                        (type.rawType as Class<*>).genericSuperclass
                    } else {
                        (type as Class<*>).genericSuperclass
                    }
            }
            return type.actualTypeArguments[0] as Class<E>
        }
    }
}
