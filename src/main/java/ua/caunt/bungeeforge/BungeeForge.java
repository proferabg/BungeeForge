package ua.caunt.bungeeforge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ua.caunt.bungeeforge.BungeeForge.MOD_ID;

@Mod(MOD_ID)
public class BungeeForge {
    public static final String MOD_ID = "bungeeforge";
    public static final List<String> integratedArgumentTypes = new ArrayList<>();
    public static final List<String> moddedArgumentTypes = new ArrayList<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    public BungeeForge(ModContainer modContainer) {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(this.getClass()
                .getResourceAsStream("/integrated_argument_types.json")))) {
            JsonObject result = new Gson().fromJson(reader, JsonObject.class);
            result.get("entries").getAsJsonArray().iterator().forEachRemaining((k) -> integratedArgumentTypes.add(k.getAsString()));
        } catch (IOException e) {
            LOGGER.error("Failed to load integrated argument types", e);
        }

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    public static boolean isIntegratedType(String identifier) {
        return integratedArgumentTypes.contains(identifier) && !isEdgeCase(identifier) && !moddedArgumentTypes.contains(identifier);
    }

    public static boolean isEdgeCase(String identifier) {
        ModList modList = ModList.get();
        return modList.isLoaded("livingthings")
                && identifier.equals("minecraft:entity");
    }
}
