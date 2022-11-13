package com.antonymo.authorizationmod.network;

import com.antonymo.authorizationmod.client.PasswordHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

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

    public static void handle(MessageRequestLogin message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var ip = Objects.requireNonNull(Minecraft.getInstance().getCurrentServer()).ip;
            PasswordHolder.instance().get(ip).ifPresent(p -> NetworkLoader.INSTANCE.sendToServer(new MessageLogin(p)));
        });
        ctx.get().setPacketHandled(true);
    }
}
