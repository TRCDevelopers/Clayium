package com.github.trc.clayium.common.util

import com.mojang.authlib.GameProfile
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayer

//TODO: is this really needed?
class ClayiumFakePlayer(
    world: WorldServer,
    profile: GameProfile,
) : FakePlayer(world, profile) {
    init {
        // to avoid NPE
        this.connection = FakeServerHandler(this)
    }

    companion object {
        private val fakePlayers = mutableMapOf<GameProfile, ClayiumFakePlayer>()
        fun get(world: WorldServer, profile: GameProfile): ClayiumFakePlayer {
            return ClayiumFakePlayer(world, profile)
        }
    }
}

/**
 * all methods are empty
 */
class FakeServerHandler(
    player: FakePlayer,
) : NetHandlerPlayServer(player.server, NetworkManager(EnumPacketDirection.SERVERBOUND), player) {
    override fun update() {}
    override fun getNetworkManager(): NetworkManager = this.networkManager
    override fun disconnect(textComponent: ITextComponent) {}
    override fun processInput(packetIn: CPacketInput) { }
    override fun processVehicleMove(packetIn: CPacketVehicleMove) { }
    override fun processConfirmTeleport(packetIn: CPacketConfirmTeleport) { }
    override fun handleRecipeBookUpdate(p_191984_1_: CPacketRecipeInfo) { }
    override fun handleSeenAdvancements(p_194027_1_: CPacketSeenAdvancements) { }
    override fun processPlayer(packetIn: CPacketPlayer) { }
    override fun setPlayerLocation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) { }
    override fun setPlayerLocation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, relativeSet: Set<SPacketPlayerPosLook.EnumFlags?>) { }
    override fun processPlayerDigging(packetIn: CPacketPlayerDigging) { }
    override fun processTryUseItemOnBlock(packetIn: CPacketPlayerTryUseItemOnBlock) { }
    override fun processTryUseItem(packetIn: CPacketPlayerTryUseItem) { }
    override fun handleSpectate(packetIn: CPacketSpectate) { }
    override fun handleResourcePackStatus(packetIn: CPacketResourcePackStatus) { }
    override fun processSteerBoat(packetIn: CPacketSteerBoat) { }
    override fun onDisconnect(reason: ITextComponent) { }
    override fun sendPacket(packetIn: Packet<*>) { }
    override fun processHeldItemChange(packetIn: CPacketHeldItemChange) { }
    override fun processChatMessage(packetIn: CPacketChatMessage) { }
    override fun handleAnimation(packetIn: CPacketAnimation) { }
    override fun processEntityAction(packetIn: CPacketEntityAction) { }
    override fun processUseEntity(packetIn: CPacketUseEntity) { }
    override fun processClientStatus(packetIn: CPacketClientStatus) { }
    override fun processCloseWindow(packetIn: CPacketCloseWindow) { }
    override fun processClickWindow(packetIn: CPacketClickWindow) { }
    override fun func_194308_a(p_194308_1_: CPacketPlaceRecipe) { }
    override fun processEnchantItem(packetIn: CPacketEnchantItem) { }
    override fun processCreativeInventoryAction(packetIn: CPacketCreativeInventoryAction) { }
    override fun processConfirmTransaction(packetIn: CPacketConfirmTransaction) { }
    override fun processUpdateSign(packetIn: CPacketUpdateSign) { }
    override fun processKeepAlive(packetIn: CPacketKeepAlive) { }
    override fun processPlayerAbilities(packetIn: CPacketPlayerAbilities) { }
    override fun processTabComplete(packetIn: CPacketTabComplete) { }
    override fun processClientSettings(packetIn: CPacketClientSettings) { }
    override fun processCustomPayload(packetIn: CPacketCustomPayload) { }
}