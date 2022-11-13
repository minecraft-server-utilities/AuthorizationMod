package com.antonymo.authorizationmod.server;

import com.antonymo.authorizationmod.Config;
import com.antonymo.authorizationmod.Constants;
import com.antonymo.authorizationmod.server.handler.PlayerLoginHandler;
import com.antonymo.authorizationmod.server.storage.Storage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = Constants.MODID)
public final class ServerLoader {

    public static void serverSetup(@SuppressWarnings("unused") FMLDedicatedServerSetupEvent event) {
        // NO-OP
    }

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent e) throws RuntimeException {
        Storage.initialize(Config.SERVER.storageProvider.get());

        PlayerLoginHandler.initLoginHandler(Config.SERVER.plugins.get().stream().map(ResourceLocation::new));
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent e) {
        PlayerLoginHandler.instance().stop();
    }
}
