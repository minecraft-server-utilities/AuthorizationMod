package com.antonymo.authorizationmod;

import com.antonymo.authorizationmod.command.CommandLoader;
import com.antonymo.authorizationmod.network.NetworkLoader;
import com.antonymo.authorizationmod.server.ServerLoader;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Constants.MODID)
public class AuthorizationMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public AuthorizationMod() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(CommandLoader::commonSetup);
        modEventBus.addListener(ServerLoader::serverSetup);
        modEventBus.addListener((FMLCommonSetupEvent e) -> NetworkLoader.registerPackets());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }
}
