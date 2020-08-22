package com.wither.useful_commands.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.wither.useful_commands.command.argument.BlockMirrorArgumentType;
import com.wither.useful_commands.command.argument.BlockRotationArgumentType;
import com.wither.useful_commands.command.argument.StructureSaveModeArgument;
import com.wither.useful_commands.mixin.structure.StructureManagerAccessorMixin;
import com.wither.useful_commands.server.world.StructureSaveMode;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.LiteralText;
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
                .then(literal("save")
                        .then(argument("name", IdentifierArgumentType.identifier())
                                .then(argument("from", Vec3ArgumentType.vec3())
                                        .then(argument("to", Vec3ArgumentType.vec3())
                                                .executes(ctx -> executeSaveStructure(
                                                        ctx,
                                                        IdentifierArgumentType.getIdentifier(ctx, "name"),
                                                        Vec3ArgumentType.getVec3(ctx, "from"),
                                                        Vec3ArgumentType.getVec3(ctx, "to"),
                                                        false,
                                                        StructureSaveMode.MEMORY
                                                ))
                                                .then(argument("include_entities", BoolArgumentType.bool())
                                                        .executes(ctx -> executeSaveStructure(
                                                                ctx,
                                                                IdentifierArgumentType.getIdentifier(ctx, "name"),
                                                                Vec3ArgumentType.getVec3(ctx, "from"),
                                                                Vec3ArgumentType.getVec3(ctx, "to"),
                                                                BoolArgumentType.getBool(ctx, "include_entities"),
                                                                StructureSaveMode.MEMORY
                                                        ))
                                                        .then(argument("save_mode", StructureSaveModeArgument.structureSaveMode())
                                                                .executes(ctx -> executeSaveStructure(
                                                                        ctx,
                                                                        IdentifierArgumentType.getIdentifier(ctx, "name"),
                                                                        Vec3ArgumentType.getVec3(ctx, "from"),
                                                                        Vec3ArgumentType.getVec3(ctx, "to"),
                                                                        BoolArgumentType.getBool(ctx, "include_entities"),
                                                                        StructureSaveModeArgument.getStructureSaveMode(ctx, "save_mode")
                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(literal("load")
                        .then(argument("name", IdentifierArgumentType.identifier())
                                .suggests(SUGGESTION_PROVIDER)
                                .executes(ctx -> executeLoadStructure(
                                        ctx,
                                        IdentifierArgumentType.getIdentifier(ctx, "name"),
                                        ctx.getSource().getPosition(),
                                        BlockRotation.NONE,
                                        BlockMirror.NONE,
                                        true
                                ))
                                .then(argument("to", Vec3ArgumentType.vec3(false))
                                        .executes(ctx -> executeLoadStructure(
                                                ctx,
                                                IdentifierArgumentType.getIdentifier(ctx, "name"),
                                                Vec3ArgumentType.getVec3(ctx, "to"),
                                                BlockRotation.NONE,
                                                BlockMirror.NONE,
                                                true
                                        ))
                                        .then(argument("rotation", BlockRotationArgumentType.blockRotation())
                                                .executes(ctx -> executeLoadStructure(
                                                        ctx,
                                                        IdentifierArgumentType.getIdentifier(ctx, "name"),
                                                        Vec3ArgumentType.getVec3(ctx, "to"),
                                                        BlockRotationArgumentType.getBlockRotation(ctx, "rotation"),
                                                        BlockMirror.NONE,
                                                        true
                                                ))
                                                .then(argument("mirror", BlockMirrorArgumentType.blockMirror())
                                                        .executes(ctx -> executeLoadStructure(
                                                                ctx,
                                                                IdentifierArgumentType.getIdentifier(ctx, "name"),
                                                                Vec3ArgumentType.getVec3(ctx, "to"),
                                                                BlockRotationArgumentType.getBlockRotation(ctx, "rotation"),
                                                                BlockMirrorArgumentType.getBlockMirror(ctx, "mirror"),
                                                                true
                                                        ))
                                                        .then(argument("include_entities", BoolArgumentType.bool())
                                                                .executes(ctx -> executeLoadStructure(
                                                                        ctx,
                                                                        IdentifierArgumentType.getIdentifier(ctx, "name"),
                                                                        Vec3ArgumentType.getVec3(ctx, "to"),
                                                                        BlockRotationArgumentType.getBlockRotation(ctx, "rotation"),
                                                                        BlockMirrorArgumentType.getBlockMirror(ctx, "mirror"),
                                                                        BoolArgumentType.getBool(ctx, "include_entities")
                                                                ))
                                                        )
                                                )
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
            throw new SimpleCommandExceptionType(new LiteralText("Invalid structure identifier")).create();
        }

        StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(mirror).setRotation(rotation).setIgnoreEntities(!includeEntities).setChunkPosition(null);
        newStructure.place(context.getSource().getWorld(), position, structurePlacementData, createRandom(0));
        return Command.SINGLE_SUCCESS;
    }

    public static int executeSaveStructure(CommandContext<ServerCommandSource> context, Identifier name, Vec3d from, Vec3d to, boolean includeEntities, StructureSaveMode saveMode) throws CommandSyntaxException {
        BlockPos fromPos = new BlockPos(from);
        BlockPos toPos = new BlockPos(to);
        BlockPos extents = new BlockPos(Math.abs(to.getX() - from.getX()), Math.abs(to.getY() - from.getY()), Math.abs(to.getZ() - from.getZ()));
        ServerWorld world = context.getSource().getWorld();
        StructureManager structureManager = world.getStructureManager();

        //This is required because structure.saveFromWorld takes only positive sizes and we need to compensate the fromPos for that
        System.out.println(Math.signum(fromPos.getX()) == -1);
        if (toPos.getX() < fromPos.getX()) {
            fromPos = new BlockPos(toPos.getX(), fromPos.getY(), fromPos.getZ());
        }
        if (toPos.getY() < fromPos.getY()) {
            fromPos = new BlockPos(fromPos.getX(), toPos.getY(), fromPos.getZ());
        }
        if (toPos.getZ() < fromPos.getZ()) {
            fromPos = new BlockPos(fromPos.getX(), fromPos.getY(), toPos.getZ());
        }

        Structure structure;
        try {
            structure = structureManager.getStructureOrBlank(name);
        } catch (InvalidIdentifierException e) {
            throw new SimpleCommandExceptionType(new LiteralText(e.getMessage())).create();
        }

        System.out.println(fromPos);
        System.out.println(extents);
        structure.saveFromWorld(world, fromPos, extents, includeEntities, Blocks.STRUCTURE_VOID);
        System.out.println(structure.getSize());
        if (saveMode == StructureSaveMode.DISK) {
            try {
                structureManager.saveStructure(name);
            } catch (InvalidIdentifierException e) {
                throw new SimpleCommandExceptionType(new LiteralText(e.getMessage())).create();
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static Random createRandom(long seed) {
        return seed == 0L ? new Random(Util.getMeasuringTimeMs()) : new Random(seed);
    }
}
