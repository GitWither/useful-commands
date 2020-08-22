package com.wither.useful_commands.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.wither.useful_commands.server.world.StructureSaveMode;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.LiteralText;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class StructureSaveModeArgument implements ArgumentType<StructureSaveMode> {
    private static final Collection<String> EXAMPLES = Arrays.asList("disk", "memory");

    public static StructureSaveModeArgument structureSaveMode() {
        return new StructureSaveModeArgument();
    }

    public static <S> StructureSaveMode getStructureSaveMode(CommandContext<S> context, String name) {
        return context.getArgument(name, StructureSaveMode.class);
    }

    @Override
    public StructureSaveMode parse(StringReader reader) throws CommandSyntaxException {
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
            case "disk":
                return StructureSaveMode.DISK;
            case "memory":
                return StructureSaveMode.MEMORY;
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
