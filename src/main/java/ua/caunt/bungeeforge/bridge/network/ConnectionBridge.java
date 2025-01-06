package ua.caunt.bungeeforge.bridge.network;

import com.mojang.authlib.properties.Property;

import java.util.Optional;
import java.util.UUID;

public interface ConnectionBridge {
    void setSpoofedAddress(String spoofedAddress);
    void setSpoofedId(UUID spoofedId);
    void setSpoofedProperties(Property[] spoofedProperties);
    Optional<String> getSpoofedAddress();
    Optional<UUID> getSpoofedId();
    Optional<Property[]> getSpoofedProperties();
    boolean hasSpoofedProfile();
}