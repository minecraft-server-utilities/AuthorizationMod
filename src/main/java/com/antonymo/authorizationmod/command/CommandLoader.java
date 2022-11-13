package com.antonymo.authorizationmod.command;

import com.antonymo.authorizationmod.Constants;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public final class CommandLoader {

    public static void commonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
        // NO-OP
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        Command.register(event.getDispatcher());
    }

}
