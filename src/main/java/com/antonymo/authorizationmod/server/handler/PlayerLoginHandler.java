package com.antonymo.authorizationmod.server.handler;

import com.antonymo.authorizationmod.AuthorizationMod;
import com.antonymo.authorizationmod.network.MessageCachePassword;
import com.antonymo.authorizationmod.network.NetworkLoader;
import com.antonymo.authorizationmod.server.Registries;
import com.antonymo.authorizationmod.server.storage.Storage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@OnlyIn(Dist.DEDICATED_SERVER)
public final class PlayerLoginHandler {
    private static PlayerLoginHandler INSTANCE;

    private final Set<String> authorizedPlayers = ConcurrentHashMap.newKeySet();
    private final Set<Login> loginList = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder()
            .setNameFormat("authorizationmod-Worker-%d")
            .build());
    private final Map<ResourceLocation, HandlerPlugin> plugins = new ConcurrentHashMap<>();

    private PlayerLoginHandler(@NotNull Stream<ResourceLocation> plugins) {
        // Load plugins
        plugins.forEach(this::loadPlugin);
    }

    public void loadPlugin(ResourceLocation rl) {
        if (this.plugins.containsKey(rl)) return;
        AuthorizationMod.LOGGER.info("Loading plugin {}", rl.toString());
        HandlerPlugin plugin = Registries.PLUGINS.get(rl).orElseThrow(() -> {
            return new IllegalArgumentException("No such plugin found: " + rl);
        }).get();

        // Should not be possible though
        Optional.ofNullable(this.plugins.put(rl, plugin)).ifPresent(HandlerPlugin::disable);
        plugin.enable(executor);
    }

    public static void initLoginHandler(Stream<ResourceLocation> pluginList) {
        if (INSTANCE != null) throw new IllegalStateException();
        INSTANCE = new PlayerLoginHandler(pluginList);
    }

    // Singleton
    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public void login(ServerPlayer player, String password) {
        var name = Login.getName(player);
        var login = getLoginByName(name);
        if (login == null) {
            return;
        }

        if (!Storage.instance().storageProvider.checkPassword(name, password)) {
            var message = Component.literal("Wrong password");
            player.sendSystemMessage(message);
            return;
        }

        var message = Component.literal("Login success");
        player.sendSystemMessage(message);

        AuthorizationMod.LOGGER.info("Player " + name + " has successfully logged in.");

        NetworkLoader.INSTANCE.send(new MessageCachePassword(password), PacketDistributor.PLAYER.with(player));
        loginList.remove(login);
        postLogin(player, login);
    }

    public void playerJoin(final ServerPlayer player) {
        var name = Login.getName(player);
        AuthorizationMod.LOGGER.info("Player name: " + name);
        AuthorizationMod.LOGGER.info("Registered names: " + Storage.instance().storageProvider
                .getAllRegisteredUsername()
                .stream()
                .reduce("", (a, b) -> a + ", " + b));
        if (!Storage.instance().storageProvider.registered(name)) {
            player.connection.disconnect(Component.literal("You are not authorized to join this server"));
            return;
        }

        authorizedPlayers.add(name);

        Login login = new Login(player);
        loginList.add(login);
        plugins.values().forEach(p -> p.preLogin(player, login));

        var message = Component.literal("Awaiting login");
        player.sendSystemMessage(message);
    }

    public void playerLeave(final ServerPlayer player) {
        var name = Login.getName(player);
        if (!authorizedPlayers.contains(name)) {
            return;
        }

        authorizedPlayers.remove(name);

        loginList.removeIf(l -> l.name.equals(name));
        plugins.values().forEach(p -> p.preLogout(player));
    }

    public void postLogin(final ServerPlayer player, final Login login) {
        plugins.values().forEach(p -> p.postLogin(player, login));
    }

    public void stop() {
        AuthorizationMod.LOGGER.info("Shutting down player login handler");
        AuthorizationMod.LOGGER.info("Disabling all plugins");
        this.plugins.values().forEach(HandlerPlugin::disable);
        this.plugins.clear();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                AuthorizationMod.LOGGER.error("Timed out waiting player login handler to terminate.");
            }
        } catch (InterruptedException ignore) {
            AuthorizationMod.LOGGER.error("Interrupted when waiting player login handler to terminate.");
        }
    }

    @Nullable
    private Login getLoginByName(String name) {
        return loginList.stream().filter(l -> l.name.equals(name)).findAny().orElse(null);
    }
}
