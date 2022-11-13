package com.antonymo.authorizationmod.server.handler.plugins;

import com.antonymo.authorizationmod.AuthorizationMod;
import com.antonymo.authorizationmod.server.handler.HandlerPlugin;
import com.antonymo.authorizationmod.server.handler.Login;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RestrictGameType implements HandlerPlugin {
    private final Map<ServerPlayer, GameType> gameTypeMap = new ConcurrentHashMap<>();

    @Override
    public void preLogin(ServerPlayer player, Login login) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(1, () -> {
            var mode = player.gameMode.getGameModeForPlayer();
            AuthorizationMod.LOGGER.info("Original mode: %s".formatted(mode.getName()));
            gameTypeMap.put(player, mode);
            player.setGameMode(GameType.SPECTATOR);
        }));
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(1, () -> {
            var mode = gameTypeMap.get(player);
            AuthorizationMod.LOGGER.info("Returning mode: %s".formatted(mode.getName()));
            player.setGameMode(mode);
            gameTypeMap.remove(player);
        }));
    }

    @Override
    public void preLogout(ServerPlayer player) {
        if (gameTypeMap.containsKey(player)) {
            player.setGameMode(gameTypeMap.get(player));
            gameTypeMap.remove(player);
        }
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
