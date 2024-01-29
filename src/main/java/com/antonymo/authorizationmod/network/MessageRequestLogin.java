package com.antonymo.authorizationmod.network;

import com.antonymo.authorizationmod.client.PasswordHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

@SuppressWarnings({"InstantiationOfUtilityClass", "unused"})
public class MessageRequestLogin {
    public MessageRequestLogin() {
    }

    public static void encode(MessageRequestLogin msg, FriendlyByteBuf buffer) {
        // NO-OP
    }

    public static MessageRequestLogin decode(FriendlyByteBuf buffer) {
        return new MessageRequestLogin();
    }

    public static void handle(MessageRequestLogin message, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            var ip = Objects.requireNonNull(Minecraft.getInstance().getCurrentServer()).ip;
            PasswordHolder.instance().get(ip).ifPresent(p -> NetworkLoader.INSTANCE.send(new MessageLogin(p), ctx.getConnection()));
        });
        ctx.setPacketHandled(true);
    }
}
