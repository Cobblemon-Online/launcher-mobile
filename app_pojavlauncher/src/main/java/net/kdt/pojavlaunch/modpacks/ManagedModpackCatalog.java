package net.kdt.pojavlaunch.modpacks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ManagedModpackCatalog {

    private ManagedModpackCatalog() {
    }

    public static final String DEFAULT_PACK_ID =
            "cobblemon-online";

    private static final List<ManagedModpack> PACKS =
            Collections.unmodifiableList(
                    Arrays.asList(

                            new ManagedModpack(
                                    "cobblemon-online",
                                    "COBBLEMON ONLINE",
                                    "modpacks/cobblemon_online.mrpack",
                                    "1.21.1",
                                    "3.3.22",
                                    "PARA CELULARES FORTES"
                            )

                    )
            );

    public static List<ManagedModpack> getPacks() {
        return PACKS;
    }

    public static ManagedModpack getById(
            String id
    ) {

        for (ManagedModpack pack : PACKS) {

            if (pack.getId().equals(id)) {
                return pack;
            }
        }

        return null;
    }

    public static ManagedModpack getDefault() {

        return getById(
                DEFAULT_PACK_ID
        );
    }
}