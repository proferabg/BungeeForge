package ua.caunt.bungeeforge.mixin.network.protocol.handshake;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ua.caunt.bungeeforge.bridge.network.protocol.handshake.ClientIntentionPacketBridge;

import java.util.UUID;

@Mixin(ClientIntentionPacket.class)
@Implements(@Interface(iface = ClientIntentionPacketBridge.class, prefix = "bungee$"))
public abstract class ClientIntentionPacketMixin {
    @Unique
    private static String bungee$spoofedAddress;
    @Unique
    private static UUID bungee$spoofedId;
    @Unique
    private static Property[] bungee$spoofedProperties;
    @Unique
    private static final Gson bungee$gson = new Gson();

    @Redirect(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readUtf(I)Ljava/lang/String;"))
    private static String readUtf(FriendlyByteBuf buf, int length) {
        String data = buf.readUtf(Short.MAX_VALUE);
        String[] chunks = data.split("\0");

        if (chunks.length <= 2)
            return data;

        Property[] properties = bungee$gson.fromJson(chunks[3], Property[].class);

        bungee$spoofedAddress = chunks[1];
        bungee$spoofedId = UUID.fromString(bungeeForge$ensureDashesInUuid(chunks[2]));
        bungee$spoofedProperties = properties;

        return chunks[1];
    }

    @Unique
    private static String bungeeForge$ensureDashesInUuid(String source) {
        if (source.length() > 32)
            return source;

        var builder = new StringBuilder(source);
        builder.insert(8, "-");
        builder.insert(13, "-");
        builder.insert(18, "-");
        builder.insert(23, "-");

        return builder.toString();
    }

    public String bungee$getSpoofedAddress() {
        return bungee$spoofedAddress;
    }

    public UUID bungee$getSpoofedId() {
        return bungee$spoofedId;
    }

    public Property[] bungee$getSpoofedProperties() {
        return bungee$spoofedProperties;
    }
}