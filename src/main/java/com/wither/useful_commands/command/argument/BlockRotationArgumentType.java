package com.wither.useful_commands.command.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockRotationArgumentType implements ArgumentType<BlockRotation> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0_degrees", "90_degrees", "180_degrees", "270_degrees");

    public static BlockRotationArgumentType blockRotation() {
        return new BlockRotationArgumentType();
    }

    public static <S> BlockRotation getBlockRotation(String name, CommandContext<S> context) {
        return context.getArgument(name, BlockRotation.class);
    }

    @Override
    public BlockRotation parse(StringReader reader) throws CommandSyntaxException {
        int argumentBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }
        while(reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String rotation = reader.getString().substring(argumentBeginning, reader.getCursor());
        switch (rotation) {
            default:
                throw new SimpleCommandExceptionType(new LiteralText("Invalid rotation")).createWithContext(reader);
            case "0_degrees":
                return BlockRotation.NONE;
            case "90_degrees":
                return BlockRotation.CLOCKWISE_90;
            case "180_degrees":
                return BlockRotation.CLOCKWISE_180;
            case "270_degrees":
                return BlockRotation.COUNTERCLOCKWISE_90;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EXAMPLES, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
