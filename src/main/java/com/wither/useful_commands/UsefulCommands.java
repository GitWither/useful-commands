package com.wither.useful_commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.wither.useful_commands.server.command.MotionCommand;
import com.wither.useful_commands.server.command.RideCommand;
import com.wither.useful_commands.server.command.ScoreboardRandomCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class UsefulCommands implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("hi");
        CommandRegistrationCallback.EVENT.register(((commandDispatcher, dedicated) -> {
            MotionCommand.register(commandDispatcher);
            RideCommand.register(commandDispatcher);
            ScoreboardRandomCommand.register(commandDispatcher);
        }));
    }
}
