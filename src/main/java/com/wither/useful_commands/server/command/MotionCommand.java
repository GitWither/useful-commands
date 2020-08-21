package com.wither.useful_commands.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.*;

public class MotionCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("motion")
                .then(literal("set")
                        .then(argument("targets", EntityArgumentType.entities())
                                .then(argument("motion", Vec3ArgumentType.vec3(false))
                                        .executes(ctx -> setMotion(ctx, EntityArgumentType.getEntities(ctx, "targets"), Vec3ArgumentType.getVec3(ctx, "motion")))
                                )))
                .then(literal("add")
                        .then(argument("targets", EntityArgumentType.entities())
                                .then(argument("motion", Vec3ArgumentType.vec3(false))
                                        .executes(ctx -> setMotion(ctx, EntityArgumentType.getEntities(ctx, "targets"), Vec3ArgumentType.getVec3(ctx, "motion"))))))
                .then(literal("get")
                        .then(argument("target", EntityArgumentType.entity())
                                .then(literal("x")
                                        .executes(ctx -> getMotion(ctx, EntityArgumentType.getEntity(ctx, "target"), Direction.Axis.X)))
                                .then(literal("y")
                                        .executes(ctx -> getMotion(ctx, EntityArgumentType.getEntity(ctx, "target"), Direction.Axis.Y)))
                                .then(literal("z")
                                        .executes(ctx -> getMotion(ctx, EntityArgumentType.getEntity(ctx, "target"), Direction.Axis.Z))))
                )
        );
    }

    public static int setMotion(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets, Vec3d motion) throws CommandSyntaxException {
        for (Entity entity : targets) {
            entity.setVelocity(motion);
        }

        //TODO: Figure out why this doesn't work on players
        return Command.SINGLE_SUCCESS;
    }
    public static int addMotion(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets, Vec3d motion) throws CommandSyntaxException {
        context.getSource().getPlayer().sendMessage(new LiteralText("shut up"), false);
        for (Entity entity : targets) {
            System.out.println(entity.getName());
            entity.addVelocity(motion.getX(), motion.getY(), motion.getZ());
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int getMotion(CommandContext<ServerCommandSource> context, Entity target, Direction.Axis component) {
        //TODO: Make it return the motion as a scaled int?
        switch (component) {
            case X:
                double x = target.getVelocity().getX();
                context.getSource().sendFeedback(new LiteralText(String.valueOf(x)), true);
                return Command.SINGLE_SUCCESS;
            case Y:
                double y = target.getVelocity().getY();
                context.getSource().sendFeedback(new LiteralText(String.valueOf(y)), true);
                return Command.SINGLE_SUCCESS;
            case Z:
                double z = target.getVelocity().getZ();
                context.getSource().sendFeedback(new LiteralText(String.valueOf(z)), true);
                return Command.SINGLE_SUCCESS;
            default:
                return 0;
        }
    }
}
