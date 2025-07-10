package com.github.trc.clayium.common.util

import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.client.CPacketClientSettings
import net.minecraft.network.play.client.CPacketClientStatus
import net.minecraft.network.play.client.CPacketCloseWindow
import net.minecraft.network.play.client.CPacketConfirmTeleport
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketCreativeInventoryAction
import net.minecraft.network.play.client.CPacketCustomPayload
import net.minecraft.network.play.client.CPacketEnchantItem
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketInput
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.client.CPacketPlaceRecipe
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerAbilities
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketRecipeInfo
import net.minecraft.network.play.client.CPacketResourcePackStatus
import net.minecraft.network.play.client.CPacketSeenAdvancements
import net.minecraft.network.play.client.CPacketSpectate
import net.minecraft.network.play.client.CPacketSteerBoat
import net.minecraft.network.play.client.CPacketTabComplete
import net.minecraft.network.play.client.CPacketUpdateSign
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.network.play.client.CPacketVehicleMove
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.common.util.FakePlayer

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