package ua.caunt.bungeeforge.mixin.network;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import ua.caunt.bungeeforge.bridge.network.ConnectionBridge;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.UUID;

@Mixin(Connection.class)
@Implements(@Interface(iface = ConnectionBridge.class, prefix = "bungee$", remap = Interface.Remap.NONE))
public abstract class ConnectionMixin {
    @Unique
    private String bungee$spoofedAddress;
    @Unique
    private UUID bungee$spoofedId;
    @Unique
    private Property[] bungee$spoofedProperties;
    @Shadow
    private SocketAddress address;

    public void bungee$setSpoofedAddress(String spoofedAddress) {
        this.bungee$spoofedAddress = spoofedAddress;
        this.address = new InetSocketAddress(spoofedAddress, 0);
    }

    public void bungee$setSpoofedId(UUID spoofedId) {
        this.bungee$spoofedId = spoofedId;
    }

    public void bungee$setSpoofedProperties(Property[] spoofedProperties) {
        this.bungee$spoofedProperties = spoofedProperties;
    }

    public Optional<String> bungee$getSpoofedAddress() {
        return Optional.ofNullable(bungee$spoofedAddress);
    }

    public Optional<UUID> bungee$getSpoofedId() {
        return Optional.ofNullable(bungee$spoofedId);
    }

    public Optional<Property[]> bungee$getSpoofedProperties() {
        return Optional.ofNullable(bungee$spoofedProperties);
    }

    public boolean bungee$hasSpoofedProfile() {
        return bungee$getSpoofedAddress().isPresent() && bungee$getSpoofedId().isPresent() && bungee$getSpoofedProperties().isPresent();
    }
}
