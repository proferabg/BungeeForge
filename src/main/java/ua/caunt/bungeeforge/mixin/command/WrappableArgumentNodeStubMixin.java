package ua.caunt.bungeeforge.mixin.command;

import io.netty.buffer.Unpooled;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import ua.caunt.bungeeforge.BungeeForge;

@Mixin(targets = "net.minecraft.network.protocol.game.ClientboundCommandsPacket$ArgumentNodeStub")
public abstract class WrappableArgumentNodeStubMixin {
    @Unique
    private static final int MOD_ARGUMENT_INDICATOR = -256;

    @Shadow
    @Final
    private ArgumentTypeInfo.Template<?> argumentType;

    @Shadow
    @Final
    private String id;

    @Shadow
    @Final
    private ResourceLocation suggestionId;

    /**
     * @author Daniel Voort.
     * @reason This is easier than injecting and returning before anything is written. There are viable alternatives
     *  available, but this is just the most straightforward and most development-time efficient. It is highly unlikely
     *  for other mods to try to mixin this particular function.
     */
    @Overwrite
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.id);

        var typeInfo = argumentType.type();
        var identifier = BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(typeInfo);
        var id = BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(typeInfo);

        if (identifier != null && BungeeForge.isIntegratedType(identifier.toString())) {
            buffer.writeVarInt(id);
            ((ArgumentTypeInfo) typeInfo).serializeToNetwork(argumentType, buffer);
        } else {
            buffer.writeVarInt(MOD_ARGUMENT_INDICATOR);
            buffer.writeVarInt(id);

            FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
            ((ArgumentTypeInfo) typeInfo).serializeToNetwork(argumentType, extraData);

            buffer.writeVarInt(extraData.readableBytes());
            buffer.writeBytes(extraData);

            extraData.release();
        }

        if (suggestionId != null)
            buffer.writeResourceLocation(suggestionId);
    }
}