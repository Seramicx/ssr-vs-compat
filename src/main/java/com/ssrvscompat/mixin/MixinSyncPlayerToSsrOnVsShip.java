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

/**
 * Forces {@code player.yRot}/{@code xRot} to track SSR's mouse-driven camera
 * rotation while the player is mounted on a Valkyrien Skies ship.
 *
 * <p><b>Why this is needed.</b> In normal SSR shoulder-surfing mode (camera
 * decoupled, not aiming), {@code ShoulderSurfingImpl.shouldEntityFollowCamera()}
 * returns {@code false} (see {@code ShoulderSurfingImpl.java:161-165}). Mouse
 * input goes through {@code ShoulderSurfingCamera.turn} which updates SSR's
 * own internal {@code yRot/xRot}, but does <b>not</b> update {@code player.yRot}
 * (the {@code shouldEntityFollowCamera} branch is what would, otherwise only
 * a clamped non-moving sync runs).
 *
 * <p>Meanwhile VS2's {@code MixinCamera.setupWithShipMounted} reads
 * {@code entity.getViewYRot/XRot} (i.e. {@code player.yRot}/{@code xRot})
 * to compute the camera basis. Since {@code player.yRot} doesn't move with
 * the mouse in shoulder-surfing mode, the camera VS2 produces is stuck
 * looking wherever the player's body was last rotated - not where the user
 * is mouse-aiming. Net effect: camera "decoupled" from mouse on a VS ship.
 *
 * <p><b>The fix.</b> HEAD inject on {@code Camera.setup}, which runs in
 * {@code GameRenderer.renderLevel} <em>before</em> VS2's wrap on
 * {@code LevelRenderer.prepareCullFrustum}. We sync
 * {@code player.yRot}/{@code xRot}/{@code yRotO}/{@code xRotO}/{@code yBodyRot}/
 * {@code yHeadRot} to SSR's tracked camera yaw/pitch. By the time VS2's
 * setupWithShipMounted reads these, they're the user's mouse-aim direction.
 *
 * <p>This is logically equivalent to forcing {@code shouldEntityFollowCamera = true}
 * for the duration of being mounted on a VS ship - the player's body rotates
 * with the camera, just like Epic Fight's TPS cam. Outside ships, SSR's
 * normal decoupled behavior is unchanged.
 *
 * <p>No-op unless VS2 is loaded, the player is mounted on a ship, the camera
 * is in third-person view (detached), and SSR shoulder-surfing is active.
 */
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

        // Force player rotation to track SSR's mouse-driven camera. VS2's
        // setupWithShipMounted (firing later this frame from the prepareCullFrustum
        // wrap) reads player.getViewYRot(partialTicks) = lerp(yRotO, yRot, ...) -
        // overwrite both ends of the lerp so the camera basis lands at the
        // user's actual mouse-aim direction rather than a stale body-rotation.
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
