package com.antonymo.authorizationmod.command;

import com.antonymo.authorizationmod.server.handler.PlayerLoginHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.Optional;

public class Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var commands = Commands.literal("authorization")
                .then(Commands.literal("login")
                        .then(Commands.argument("password", StringArgumentType.word())
                                .executes(Command::login)))
                .then(Commands.literal("test")
                        .executes(Command::test));

        dispatcher.register(commands);
    }

    private static int login(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(Component.literal("Attempting login"), false);

        var password = ctx.getArgument("password", String.class);
        var player = ctx.getSource().getPlayer();
        if (player != null) {
            DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> PlayerLoginHandler.instance().login(player, password));
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    private static int test(CommandContext<CommandSourceStack> ctx) {
        Optional.ofNullable(ctx.getSource().getPlayer()).ifPresent(p -> p.sendSystemMessage(Component.literal("test success")));

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
