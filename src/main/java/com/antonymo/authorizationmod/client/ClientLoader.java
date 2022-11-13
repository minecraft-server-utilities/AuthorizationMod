package com.antonymo.authorizationmod.client;

import com.antonymo.authorizationmod.AuthorizationMod;
import com.antonymo.authorizationmod.Constants;
import com.antonymo.authorizationmod.network.MessageLogin;
import com.antonymo.authorizationmod.network.NetworkLoader;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public final class ClientLoader {

    @SubscribeEvent
    public static void joinServer(ClientPlayerNetworkEvent.LoggingIn event) {
        if (event.getConnection().isMemoryConnection()) return;

        var ip = Objects.requireNonNull(Minecraft.getInstance().getCurrentServer()).ip;
        var password = PasswordHolder.instance().get(ip);
        password.ifPresentOrElse(p -> {
            AuthorizationMod.LOGGER.info("Sending login packet to the server...");
            NetworkLoader.INSTANCE.sendToServer(new MessageLogin(p));
        }, () -> AuthorizationMod.LOGGER.info("No saved password for ip %s".formatted(ip)));
    }
}
