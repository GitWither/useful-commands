package com.wither.useful_commands.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.ObjectiveArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;
import java.util.Random;

import static net.minecraft.server.command.CommandManager.*;

public class ScoreboardRandomCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode cmd = dispatcher.register(literal("random")
                .then(argument("target", ScoreHolderArgumentType.scoreHolders())
                        .suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
                        .then(argument("objective", ObjectiveArgumentType.objective())
                                .then(argument("min", IntegerArgumentType.integer())
                                        .then(argument("max", IntegerArgumentType.integer())
                                                .executes(ctx -> executeRandom(ctx, ScoreHolderArgumentType.getScoreHolders(ctx, "target"), ObjectiveArgumentType.getObjective(ctx, "objective"), IntegerArgumentType.getInteger(ctx, "min"), IntegerArgumentType.getInteger(ctx, "max")))
                                        )
                                )
                        )
                )
        );
        dispatcher.getRoot().getChild("scoreboard").getChild("players").addChild(cmd);
    }

    public static int executeRandom(CommandContext<ServerCommandSource> context, Collection<String> targets, ScoreboardObjective objective, int min, int max) throws CommandSyntaxException {
        Scoreboard scoreboard = context.getSource().getMinecraftServer().getScoreboard();

        for (String target: targets) {
            Random random = new Random();

            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(target, objective);
            scoreboardPlayerScore.setScore(random.nextInt(max + 1 - min) + min);
        }

        //TODO: Add success message

        return Command.SINGLE_SUCCESS;
    }
}
