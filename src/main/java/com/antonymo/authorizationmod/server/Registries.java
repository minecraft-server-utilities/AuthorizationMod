package com.antonymo.authorizationmod.server;

import com.antonymo.authorizationmod.Constants;
import com.antonymo.authorizationmod.server.handler.HandlerPlugin;
import com.antonymo.authorizationmod.server.handler.plugins.ResendRequest;
import com.antonymo.authorizationmod.server.handler.plugins.RestrictGameType;
import com.antonymo.authorizationmod.server.handler.plugins.RestrictMovement;
import com.antonymo.authorizationmod.server.handler.plugins.Timeout;
import com.antonymo.authorizationmod.server.storage.StorageProvider;
import com.antonymo.authorizationmod.server.storage.StorageProviderFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Registries<S> {
    private final Map<ResourceLocation, Supplier<? extends S>> plugins = new HashMap<>();

    public synchronized void register(ResourceLocation rl, Supplier<? extends S> plugin) {
        if (plugins.containsKey(rl)) {
            throw new IllegalArgumentException("Resource location " + rl.toString() + " already exists.");
        }
        plugins.put(rl, plugin);
    }

    public Optional<Supplier<? extends S>> get(ResourceLocation rl) {
        return Optional.ofNullable(plugins.get(rl));
    }

    private Registries() {
    }

    public static final Registries<HandlerPlugin> PLUGINS = new Registries<>();
    public static final Registries<StorageProvider> STORAGE_PROVIDERS = new Registries<>();

    static {
        // Default plugins
        PLUGINS.register(new ResourceLocation("authorizationmod", "resend_request"), ResendRequest::new);
        PLUGINS.register(new ResourceLocation("authorizationmod", "restrict_game_type"), RestrictGameType::new);
        PLUGINS.register(new ResourceLocation("authorizationmod", "restrict_movement"), RestrictMovement::new);
        PLUGINS.register(new ResourceLocation("authorizationmod", "timeout"), Timeout::new);

        // Default storage providers
        STORAGE_PROVIDERS.register(new ResourceLocation("authorizationmod", "file"),
                () -> mustCall(() -> new StorageProviderFile(ServerLifecycleHooks.getCurrentServer().getWorldPath(Constants.ENTRY))));
    }

    private static <S> S mustCall(Callable<S> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
