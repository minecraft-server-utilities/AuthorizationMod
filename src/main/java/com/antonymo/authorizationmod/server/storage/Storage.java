package com.antonymo.authorizationmod.server.storage;

import com.antonymo.authorizationmod.server.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public class Storage {
    public final StorageProvider storageProvider;
    private static Storage INSTANCE;

    public static Storage instance() {
        return INSTANCE;
    }

    public static void initialize(String provider) {
        if (INSTANCE == null) {
            INSTANCE = new Storage(provider);
        }
    }

    private Storage(String provider) {
        storageProvider = Registries.STORAGE_PROVIDERS.get(new ResourceLocation(provider))
                .orElseThrow(() -> new RuntimeException("Storage provider not found: " + provider))
                .get();
    }
}
