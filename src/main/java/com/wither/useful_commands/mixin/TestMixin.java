package com.wither.useful_commands.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class TestMixin {
    @Shadow protected abstract float getVelocityMultiplier();

    @Inject(at = @At(value = "HEAD"), method = "jump()V")
    private void jump(CallbackInfo cb) {
    }
}
