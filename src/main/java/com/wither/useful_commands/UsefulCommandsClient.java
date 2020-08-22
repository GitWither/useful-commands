package com.wither.useful_commands;

import com.wither.useful_commands.command.argument.BlockMirrorArgumentType;
import com.wither.useful_commands.command.argument.BlockRotationArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UsefulCommandsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArgumentTypes.register(new Identifier(UsefulCommands.MOD_ID, "block_rotation").toString(), BlockRotationArgumentType.class, new ConstantArgumentSerializer<>(BlockRotationArgumentType::blockRotation));
        ArgumentTypes.register(new Identifier(UsefulCommands.MOD_ID, "block_mirror").toString(), BlockMirrorArgumentType.class, new ConstantArgumentSerializer<>(BlockMirrorArgumentType::blockMirror));

    }
}
