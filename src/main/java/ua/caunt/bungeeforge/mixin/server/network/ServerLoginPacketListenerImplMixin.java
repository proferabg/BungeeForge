package ua.caunt.bungeeforge.mixin.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ua.caunt.bungeeforge.bridge.network.ConnectionBridge;
import ua.caunt.bungeeforge.bridge.server.network.ServerLoginPacketListenerImplBridge;

import java.util.Arrays;
import java.util.regex.Pattern;

@Mixin(ServerLoginPacketListenerImpl.class)
@Implements(@Interface(iface = ServerLoginPacketListenerImplBridge.class, prefix = "bungee$"))
public abstract class ServerLoginPacketListenerImplMixin {
    @Unique
    private static final Logger bungeeForge$LOGGER = LogUtils.getLogger();

    @Final
    @Shadow
    Connection connection;

    @Shadow
    private GameProfile authenticatedProfile;

    @Unique
    private static final Pattern PROP_PATTERN = Pattern.compile("\\w{0,16}");

    @Redirect(method = "startClientVerification(Lcom/mojang/authlib/GameProfile;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;authenticatedProfile:Lcom/mojang/authlib/GameProfile;", opcode = Opcodes.PUTFIELD))
    public void startClientVerification(net.minecraft.server.network.ServerLoginPacketListenerImpl instance, GameProfile value) {
        var connectionBridge = (ConnectionBridge)bungee$getConnection();

        if (!connectionBridge.hasSpoofedProfile()) {
            authenticatedProfile = value;
            MutableComponent component = Component.literal("If you wish to use IP forwarding, please enable it in your BungeeCord/Velocity config as well!");
            component.setStyle(component.getStyle().withColor(ChatFormatting.RED));
            connection.disconnect(component);
            return;
        }

        var gameProfile = new GameProfile(connectionBridge.getSpoofedId().get(), value.getName());
        var properties = gameProfile.getProperties();

        Arrays.stream(connectionBridge.getSpoofedProperties().get()).filter(property -> PROP_PATTERN.matcher(property.name()).matches()).forEach(property -> {
            properties.put(property.name(), property);
        });

        authenticatedProfile = gameProfile;
    }

    public Connection bungee$getConnection() {
        return connection;
    }
}
