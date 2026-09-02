package net.kdt.pojavlaunch.modpacks;

public final class ManagedModpack {

    private final String id;
    private final String name;
    private final String assetFile;

    private final String minecraftVersion;
    private final String modpackVersion;
    private final String description;

    public ManagedModpack(
            String id,
            String name,
            String assetFile,
            String minecraftVersion,
            String modpackVersion,
            String description
    ) {

        this.id = id;
        this.name = name;
        this.assetFile = assetFile;
        this.minecraftVersion = minecraftVersion;
        this.modpackVersion = modpackVersion;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAssetFile() {
        return assetFile;
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public String getModpackVersion() {
        return modpackVersion;
    }

    public String getDescription() {
        return description;
    }
}