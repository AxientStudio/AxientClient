package asia.axientstudio.axientclient.features;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.*;

public class ExternalModDetector {

    // Known client-side cheat / utility mods by mod ID
    private static final Set<String> KNOWN_EXTERNAL = new HashSet<>(Arrays.asList(
            "wurst", "meteor-client", "baritone", "xaero_minimap", "xaero_worldmap",
            "jei", "rei", "emi", "sodium", "lithium", "phosphor", "optifabric",
            "iris", "oculus", "freecam", "bobby", "tweakeroo", "minihud",
            "itemscroller", "voxelmap", "journeymap", "invtweaks", "bettersprinting",
            "autofish", "craftpresence"
    ));

    // OWN mod IDs to exclude
    private static final Set<String> OWN_MODS = new HashSet<>(Arrays.asList(
            "axientclient", "fabricloader", "fabric-api", "java", "minecraft"
    ));

    public static List<ModInfo> detectExternal() {
        List<ModInfo> detected = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            String id = mod.getMetadata().getId();
            if (OWN_MODS.contains(id)) continue;
            if (id.startsWith("fabric-")) continue; // fabric api sub-mods

            String name = mod.getMetadata().getName();
            String version = mod.getMetadata().getVersion().getFriendlyString();
            boolean isKnown = KNOWN_EXTERNAL.contains(id);

            detected.add(new ModInfo(id, name, version, isKnown));
        }
        detected.sort(Comparator.comparing(m -> m.name));
        return detected;
    }

    public static class ModInfo {
        public final String id;
        public final String name;
        public final String version;
        public final boolean isKnownExternal;

        public ModInfo(String id, String name, String version, boolean isKnownExternal) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.isKnownExternal = isKnownExternal;
        }
    }
}
