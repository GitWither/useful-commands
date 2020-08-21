package com.wither.useful_commands.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static net.minecraft.server.command.CommandManager.*;

public class StructureCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("structure")
                .then(literal("load")
                        .then(argument("name", StringArgumentType.string())
                                .then(argument("to", Vec3ArgumentType.vec3(false))
                                        .executes(ctx -> executeLoadStructure(ctx, StringArgumentType.getString(ctx, "name"), Vec3ArgumentType.getVec3(ctx, "to")))
                                )
                        )
                )
        );
    }

    public static int executeLoadStructure(CommandContext<ServerCommandSource> context, String name, Vec3d to) throws CommandSyntaxException {
        StructureManager structureManager = context.getSource().getMinecraftServer().getStructureManager();
        BlockPos position = new BlockPos(to);

        Structure newStructure;
        try {
            newStructure = structureManager.getStructure(new Identifier(name));
        } catch (InvalidIdentifierException ex) {
            return 0;
        }

        StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setChunkPosition(null);
        newStructure.place(context.getSource().getWorld(), position, structurePlacementData, createRandom(0));
        return Command.SINGLE_SUCCESS;
    }

    private static Random createRandom(long seed) {
        return seed == 0L ? new Random(Util.getMeasuringTimeMs()) : new Random(seed);
    }
}
