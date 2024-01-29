package com.antonymo.authorizationmod.network;

import com.antonymo.authorizationmod.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkLoader {
    private static final int PROTOCOL_VERSION = 2;

    public static SimpleChannel INSTANCE = ChannelBuilder
            .named(new ResourceLocation(Constants.MODID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel();

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
                                             BiConsumer<MSG, CustomPayloadEvent.Context> handler,
                                             final NetworkDirection direction) {
        INSTANCE.messageBuilder(msg, id++, direction)
                .encoder(encoder)
                .decoder(decoder)
                .consumerNetworkThread(handler)
                .add();
    }
}
