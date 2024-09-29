package com.github.trc.clayium.common.util

import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.common.network.CNetwork
import com.github.trc.clayium.common.network.KeyInputPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.WeakHashMap

enum class KeyInput(
    // Double Supplier to keep client classes from loading
    keyBinding: () -> (() -> KeyBinding)
) {
    SPRINT({{ Minecraft.getMinecraft().gameSettings.keyBindSprint }}),
    ;

    private val mapping by lazy { WeakHashMap<EntityPlayerMP, MutBooleanPairKeyData>() }
    private lateinit var keyBinding: KeyBinding
    @SideOnly(Side.CLIENT)
    private var isKeyDown = false
    @SideOnly(Side.CLIENT)
    private var isPressed = false

    init {
        if (CUtils.isClientSide) {
            this.keyBinding = keyBinding()()
        }
    }

    fun update(player: EntityPlayerMP, isKeyDown: Boolean, isPressed: Boolean) {
        val pair = mapping.computeIfAbsent(player) { MutBooleanPairKeyData(false, false) }
        pair.isKeyDown = isKeyDown
        pair.isPressed = isPressed
    }

    @SideOnly(Side.CLIENT)
    fun isKeyDown(): Boolean {
        return this.keyBinding.isKeyDown
    }

    @SideOnly(Side.CLIENT)
    fun isPressed(): Boolean {
        return this.keyBinding.isPressed
    }

    fun isKeyDown(player: EntityPlayerMP): Boolean {
        return mapping[player]?.isKeyDown == true
    }

    fun isPressed(player: EntityPlayerMP): Boolean {
        return mapping[player]?.isPressed == true
    }

    class MutBooleanPairKeyData(
        var isKeyDown: Boolean,
        var isPressed: Boolean
    )

    companion object {
        @SubscribeEvent
        @Suppress("unused")
        fun onKeyInput(e: InputEvent.KeyInputEvent) {
            var updating: MutableList<KeyInput>? = null
            for (key in KeyInput.entries) {
                val prevIsKeyDown = key.isKeyDown
                val prevIsPressed = key.isPressed
                key.isKeyDown = key.isKeyDown()
                key.isPressed = key.isPressed()

                if (prevIsKeyDown != key.isKeyDown || prevIsPressed != key.isPressed) {
                    if (updating == null) updating = mutableListOf()
                    updating.add(key)
                }
            }
            if (updating?.isNotEmpty() == true) {
                CNetwork.channel.sendToServer(KeyInputPacket(updating))
            }
        }
    }
}