package com.wither.useful_commands.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.wither.useful_commands.command.argument.BlockMirrorArgumentType;
import com.wither.useful_commands.command.argument.BlockRotationArgumentType;
import com.wither.useful_commands.mixin.structure.StructureManagerAccessorMixin;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.*;

public class StructureCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionBuilder) -> {
        Set<Identifier> identifiers = ((StructureManagerAccessorMixin)commandContext.getSource().getMinecraftServer().getStructureManager()).getStructures().keySet();
        return CommandSource.suggestIdentifiers(identifiers, suggestionBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("structure")
                .then(literal("load")
                        .then(argument("name", IdentifierArgumentType.identifier())
                                .suggests(SUGGESTION_PROVIDER)
                                .executes(ctx -> executeLoadStructure(ctx, IdentifierArgumentType.getIdentifier(ctx, "name"), ctx.getSource().getPosition(), BlockRotation.NONE, BlockMirror.NONE, true))
                                .then(argument("to", Vec3ArgumentType.vec3(false))
                                        .executes(ctx -> executeLoadStructure(ctx, IdentifierArgumentType.getIdentifier(ctx, "name"), Vec3ArgumentType.getVec3(ctx, "to"), BlockRotation.NONE, BlockMirror.NONE, true))
                                        .then(argument("rotation", BlockRotationArgumentType.blockRotation())
                                                .executes(ctx -> executeLoadStructure(ctx, IdentifierArgumentType.getIdentifier(ctx, "name"), Vec3ArgumentType.getVec3(ctx, "to"), BlockRotationArgumentType.getBlockRotation("rotation", ctx), BlockMirror.NONE, true))
                                                .then(argument("mirror", BlockMirrorArgumentType.blockMirror())
                                                        .executes(ctx -> executeLoadStructure(ctx, IdentifierArgumentType.getIdentifier(ctx, "name"), Vec3ArgumentType.getVec3(ctx, "to"), BlockRotationArgumentType.getBlockRotation("rotation", ctx), BlockMirrorArgumentType.getBlockMirror("mirror", ctx), true)))
                                        )
                                )
                        )
                )
        );
    }

    public static int executeLoadStructure(CommandContext<ServerCommandSource> context, Identifier name, Vec3d to, BlockRotation rotation, BlockMirror mirror, boolean includeEntities) throws CommandSyntaxException {
        StructureManager structureManager = context.getSource().getMinecraftServer().getStructureManager();
        BlockPos position = new BlockPos(to);

        Structure newStructure;
        try {
            newStructure = structureManager.getStructure(name);
        } catch (InvalidIdentifierException ex) {
            return 0;
        }

        StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(mirror).setRotation(rotation).setChunkPosition(null);
        newStructure.place(context.getSource().getWorld(), position, structurePlacementData, createRandom(0));
        return Command.SINGLE_SUCCESS;
    }

    private static Random createRandom(long seed) {
        return seed == 0L ? new Random(Util.getMeasuringTimeMs()) : new Random(seed);
    }
}
