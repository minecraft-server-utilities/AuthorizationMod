package com.antonymo.authorizationmod.network;

import com.antonymo.authorizationmod.client.PasswordHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class MessageCachePassword {
    private final String pwd;

    public MessageCachePassword(String pwd) {
        this.pwd = pwd;
    }

    public static void encode(MessageCachePassword packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.pwd.length());
        buf.writeCharSequence(packet.pwd, StandardCharsets.UTF_8);
    }

    public static MessageCachePassword decode(FriendlyByteBuf buffer) {
        int len = buffer.readInt();
        return new MessageCachePassword(buffer.readCharSequence(len, StandardCharsets.UTF_8).toString());
    }

    public static void handle(MessageCachePassword message, Supplier<NetworkEvent.Context> ctx) {
        var ip = Objects.requireNonNull(Minecraft.getInstance().getCurrentServer()).ip;
        PasswordHolder.instance().set(ip, message.pwd);
        Optional.ofNullable(Minecraft.getInstance().player).ifPresent(p -> p.sendSystemMessage(Component.literal("Cached password")));

        ctx.get().setPacketHandled(true);
    }
}
