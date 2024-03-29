package com.antonymo.authorizationmod.network;

import com.antonymo.authorizationmod.server.handler.PlayerLoginHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.nio.charset.StandardCharsets;

public class MessageLogin {
    private final String pwd;

    public MessageLogin(String pwd) {
        this.pwd = pwd;
    }

    public static void encode(MessageLogin packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.pwd.length());
        buf.writeCharSequence(packet.pwd, StandardCharsets.UTF_8);
    }

    public static MessageLogin decode(FriendlyByteBuf buffer) {
        int len = buffer.readInt();
        return new MessageLogin(buffer.readCharSequence(len, StandardCharsets.UTF_8).toString());
    }

    public static void handle(MessageLogin message, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player != null) {
            PlayerLoginHandler.instance().login(player, message.pwd);
        }
        ctx.setPacketHandled(true);
    }
}
