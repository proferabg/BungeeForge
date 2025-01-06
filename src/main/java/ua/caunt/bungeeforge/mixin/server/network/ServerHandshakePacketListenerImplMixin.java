package ua.caunt.bungeeforge.mixin.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.caunt.bungeeforge.bridge.network.ConnectionBridge;
import ua.caunt.bungeeforge.bridge.network.protocol.handshake.ClientIntentionPacketBridge;

@Mixin(ServerHandshakePacketListenerImpl.class)
public abstract class ServerHandshakePacketListenerImplMixin {
    @Final
    @Shadow
    private Connection connection;

    @Inject(method = "handleIntention(Lnet/minecraft/network/protocol/handshake/ClientIntentionPacket;)V", at = @At("HEAD"))
    public void handleIntention(ClientIntentionPacket p_9975_, CallbackInfo ci) {
        ClientIntentionPacketBridge clientIntentionPacketBridge = (ClientIntentionPacketBridge) (Object) p_9975_;
        ConnectionBridge connectionBridge = (ConnectionBridge) connection;

        connectionBridge.setSpoofedAddress(clientIntentionPacketBridge.getSpoofedAddress());
        connectionBridge.setSpoofedId(clientIntentionPacketBridge.getSpoofedId());
        connectionBridge.setSpoofedProperties(clientIntentionPacketBridge.getSpoofedProperties());
    }
}
