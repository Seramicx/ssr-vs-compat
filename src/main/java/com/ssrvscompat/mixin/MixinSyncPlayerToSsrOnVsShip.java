package com.ssrvscompat.mixin;

import com.ssrvscompat.compat.ShoulderSurfingHelper;
import com.ssrvscompat.compat.ValkyrienSkiesHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinSyncPlayerToSsrOnVsShip {

    @Inject(method = "setup", at = @At("HEAD"))
    private void ssrvscompat$syncPlayerRotationToSsr(BlockGetter level, Entity entity,
                                                     boolean detached, boolean thirdPersonReverse,
                                                     float partialTick, CallbackInfo ci) {
        if (!detached) return;
        if (!(entity instanceof LocalPlayer player)) return;
        if (!ShoulderSurfingHelper.isShoulderSurfingActive()) return;
        if (!ValkyrienSkiesHelper.isMountedOnShip(player)) return;

        float ssrYaw = ShoulderSurfingHelper.getCameraYRot();
        float ssrPitch = ShoulderSurfingHelper.getCameraXRot();
        if (Float.isNaN(ssrYaw) || Float.isNaN(ssrPitch)) return;

        player.setYRot(ssrYaw);
        player.setXRot(ssrPitch);
        player.yRotO = ssrYaw;
        player.xRotO = ssrPitch;
        player.yBodyRot = ssrYaw;
        player.yBodyRotO = ssrYaw;
        player.yHeadRot = ssrYaw;
        player.yHeadRotO = ssrYaw;
    }
}
