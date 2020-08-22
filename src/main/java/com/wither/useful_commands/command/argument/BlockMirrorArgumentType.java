package com.wither.useful_commands.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockMirrorArgumentType implements ArgumentType<BlockMirror> {

    private static final Collection<String> EXAMPLES = Arrays.asList("none", "x", "z");

    public static BlockMirrorArgumentType blockMirror() {
        return new BlockMirrorArgumentType();
    }

    public static <S> BlockMirror getBlockMirror(String name, CommandContext<S> context) {
        return context.getArgument(name, BlockMirror.class);
    }

    @Override
    public BlockMirror parse(StringReader reader) throws CommandSyntaxException {
        int argumentBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }
        while(reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String mirror = reader.getString().substring(argumentBeginning, reader.getCursor());
        switch (mirror) {
            default:
                throw new SimpleCommandExceptionType(new LiteralText("Invalid mirror")).createWithContext(reader);
            case "none":
                return BlockMirror.NONE;
            case "x":
                return BlockMirror.FRONT_BACK;
            case "z":
                return BlockMirror.LEFT_RIGHT;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EXAMPLES, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
