package ua.caunt.bungeeforge;

import java.util.List;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import static ua.caunt.bungeeforge.BungeeForge.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> MODDED_ARGUMENT_TYPES = BUILDER
            .comment( "List of argument types that are not vanilla but are integrated into the server (found in the Vanilla registry)")
            .defineListAllowEmpty("moddedArgumentTypes", List.of("livingthings:sampler_types"), Config::validate);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validate(final Object obj)
    {
        return obj instanceof String;
    }


    @SubscribeEvent
    static void onLoad(ServerAboutToStartEvent event)
    {

        // convert the list of strings into a set of items
        BungeeForge.moddedArgumentTypes.addAll(MODDED_ARGUMENT_TYPES.get());
    }
}
