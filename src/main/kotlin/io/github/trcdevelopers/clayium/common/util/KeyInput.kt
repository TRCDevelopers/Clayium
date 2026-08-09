package io.github.trcdevelopers.clayium.common.util

import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.common.network.CNetwork
import io.github.trcdevelopers.clayium.common.network.KeyInputPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

enum class KeyInput(
    // Double Supplier to keep client classes from loading
    keyBinding: () -> (() -> KeyBinding)
) {
    SPRINT({{ Minecraft.getMinecraft().gameSettings.keyBindSprint }}),
    ;

    private val mapping by lazy { WeakHashMap<EntityPlayerMP, Boolean>() }
    private lateinit var keyBinding: KeyBinding
    @SideOnly(Side.CLIENT)
    private var isKeyDown = false

    init {
        if (CUtils.isClientSide) {
            this.keyBinding = keyBinding()()
        }
    }

    fun update(player: EntityPlayerMP, isKeyDown: Boolean) {
        this.mapping[player] = isKeyDown
    }

    @SideOnly(Side.CLIENT)
    fun isKeyDown(): Boolean {
        return this.keyBinding.isKeyDown
    }

    fun isKeyDown(player: EntityPlayerMP): Boolean {
        return mapping[player] == true
    }

    companion object {
        @SubscribeEvent
        @Suppress("unused")
        fun onKeyInput(e: InputEvent.KeyInputEvent) {
            var updating: MutableList<KeyInput>? = null
            for (key in KeyInput.entries) {
                val prevIsKeyDown = key.isKeyDown
                key.isKeyDown = key.isKeyDown()

                if (prevIsKeyDown != key.isKeyDown) {
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