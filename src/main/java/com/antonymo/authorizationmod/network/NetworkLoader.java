package com.antonymo.authorizationmod.network;

import com.antonymo.authorizationmod.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkLoader {
    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MODID, "main"),
            () -> PROTOCOL_VERSION,
            v -> true,
            v -> true
    );

    private NetworkLoader() {
        throw new UnsupportedOperationException("No instance");
    }

    public static void registerPackets() {
        registerPacket(MessageLogin.class,
                MessageLogin::encode,
                MessageLogin::decode,
                MessageLogin::handle,
                NetworkDirection.PLAY_TO_SERVER);
        registerPacket(MessageRequestLogin.class,
                MessageRequestLogin::encode,
                MessageRequestLogin::decode,
                MessageRequestLogin::handle,
                NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(MessageCachePassword.class,
                MessageCachePassword::encode,
                MessageCachePassword::decode,
                MessageCachePassword::handle,
                NetworkDirection.PLAY_TO_CLIENT);
    }

    private static int id = 0;

    private static <MSG> void registerPacket(Class<MSG> msg, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                             Function<FriendlyByteBuf, MSG> decoder,
                                             BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler,
                                             final NetworkDirection direction) {
        INSTANCE.registerMessage(id++, msg, encoder, decoder, handler, Optional.of(direction));
    }
}
