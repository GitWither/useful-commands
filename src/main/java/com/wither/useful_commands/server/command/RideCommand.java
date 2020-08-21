package com.wither.useful_commands.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.NbtCompoundTagArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.*;

public class RideCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("ride")
                .then(argument("rides", EntityArgumentType.entities())
                        .then(literal("summon_rider")
                                .then(argument("entity", EntitySummonArgumentType.entitySummon())
                                        .executes(ctx -> summonRider(ctx, EntityArgumentType.getEntities(ctx, "rides"), EntitySummonArgumentType.getEntitySummon(ctx, "entity"), new CompoundTag(), true))
                                        .then(argument("nbt", NbtCompoundTagArgumentType.nbtCompound())
                                                .executes(ctx -> summonRider(ctx, EntityArgumentType.getEntities(ctx, "rides"), EntitySummonArgumentType.getEntitySummon(ctx, "entity"), NbtCompoundTagArgumentType.getCompoundTag(ctx, "nbt"), false))
                                        )
                                )
                        )
                        .then(literal("evict_riders")
                                .executes(ctx -> evictRiders(ctx, EntityArgumentType.getEntities(ctx, "rides")))
                        )
                )
                .then(argument("riders", EntityArgumentType.entities())
                        .then(literal("summon_ride")
                                .then(argument("entity", EntitySummonArgumentType.entitySummon())
                                        .executes(ctx -> summonRide(ctx, EntityArgumentType.getEntities(ctx, "riders"), EntitySummonArgumentType.getEntitySummon(ctx, "entity"), new CompoundTag(), true))
                                        .then(argument("nbt", NbtCompoundTagArgumentType.nbtCompound())
                                                .executes(ctx -> summonRide(ctx, EntityArgumentType.getEntities(ctx, "riders"), EntitySummonArgumentType.getEntitySummon(ctx, "entity"), NbtCompoundTagArgumentType.getCompoundTag(ctx, "nbt"), false)))))
                        .then(literal("stop_riding")
                                .executes(ctx -> stopRiding(ctx, EntityArgumentType.getEntities(ctx, "riders"))))
                        .then(literal("start_riding")
                                .then(argument("ride", EntityArgumentType.entity())
                                        .executes(ctx -> addRider(ctx, EntityArgumentType.getEntities(ctx, "riders"), EntityArgumentType.getEntity(ctx, "ride")))
                                )
                        )
                )
        );
    }

    public static int summonRider(CommandContext<ServerCommandSource> context, Collection<? extends Entity> rides, Identifier rider, CompoundTag nbt, boolean initialize) throws CommandSyntaxException {
        for (Entity ride: rides) {
            BlockPos spawnPos = new BlockPos(ride.getPos());
            if (!World.method_25953(spawnPos)) {
                throw new SimpleCommandExceptionType(new TranslatableText("commands.summon.invalidPosition")).create();
            } else {
                CompoundTag compound = nbt.copy();
                compound.putString("id", rider.toString());
                ServerWorld world = context.getSource().getWorld();
                Entity finalEntity = EntityType.loadEntityWithPassengers(compound, world, (newEntity) -> {
                    newEntity.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), newEntity.yaw, newEntity.pitch);
                    return newEntity;
                });
                System.out.println(finalEntity);
                if (finalEntity == null) {
                    throw new SimpleCommandExceptionType(new TranslatableText("commands.summon.failed")).create();
                } else {
                    if (initialize && finalEntity instanceof MobEntity) {
                        ((MobEntity) finalEntity).initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.COMMAND, null, null);
                    }

                    if (!world.method_30736(finalEntity)) {
                        throw new SimpleCommandExceptionType(new TranslatableText("commands.summon.failed.uuid")).create();
                    }

                    finalEntity.startRiding(ride, true);
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int summonRide(CommandContext<ServerCommandSource> context, Collection<? extends  Entity> riders, Identifier ride, CompoundTag nbt, boolean initialize) throws CommandSyntaxException {
        for (Entity rider: riders) {
            BlockPos spawnPos = new BlockPos(rider.getPos());
            if (!World.method_25953(spawnPos)) {
                throw new SimpleCommandExceptionType(new TranslatableText("commands.summon.invalidPosition")).create();
            } else {
                CompoundTag compound = nbt.copy();
                compound.putString("id", ride.toString());
                ServerWorld world = context.getSource().getWorld();
                Entity finalEntity = EntityType.loadEntityWithPassengers(compound, world, (newEntity) -> {
                    newEntity.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), newEntity.yaw, newEntity.pitch);
                    return newEntity;
                });
                System.out.println(finalEntity);
                if (finalEntity == null) {
                    throw new SimpleCommandExceptionType(new TranslatableText("commands.summon.failed")).create();
                } else {
                    if (initialize && finalEntity instanceof MobEntity) {
                        ((MobEntity) finalEntity).initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.COMMAND, null, null);
                    }

                    if (!world.method_30736(finalEntity)) {
                        throw new SimpleCommandExceptionType(new TranslatableText("commands.summon.failed.uuid")).create();
                    }

                    rider.startRiding(finalEntity, true);
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int evictRiders(CommandContext<ServerCommandSource> context, Collection<? extends  Entity> rides) {
        for (Entity ride:
             rides) {
            for (Entity rider:
                 ride.getPassengerList()) {
                rider.stopRiding();
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int stopRiding(CommandContext<ServerCommandSource> context, Collection<? extends Entity> riders) {
        for (Entity rider:
             riders) {
            rider.stopRiding();
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int addRider(CommandContext<ServerCommandSource> context, Collection<? extends Entity> riders, Entity ride) {
        for (Entity rider:
             riders) {
            rider.startRiding(ride, true);
        }
        return Command.SINGLE_SUCCESS;
    }
}
