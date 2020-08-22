package com.wither.useful_commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.wither.useful_commands.command.argument.BlockRotationArgumentType;
import com.wither.useful_commands.server.command.MotionCommand;
import com.wither.useful_commands.server.command.RideCommand;
import com.wither.useful_commands.server.command.ScoreboardRandomCommand;
import com.wither.useful_commands.server.command.StructureCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

public class UsefulCommands implements ModInitializer {
    public static final String MOD_ID = "useful_commands";

    @Override
    public void onInitialize() {
        System.out.println("hi");
        CommandRegistrationCallback.EVENT.register(((commandDispatcher, dedicated) -> {
            MotionCommand.register(commandDispatcher);
            RideCommand.register(commandDispatcher);
            ScoreboardRandomCommand.register(commandDispatcher);
            StructureCommand.register(commandDispatcher);
        }));
    }
}
