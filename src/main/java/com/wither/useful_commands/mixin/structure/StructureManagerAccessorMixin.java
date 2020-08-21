package com.wither.useful_commands.mixin.structure;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StructureManager.class)
public interface StructureManagerAccessorMixin {
    @Accessor
    Map<Identifier, Structure> getStructures();
}
