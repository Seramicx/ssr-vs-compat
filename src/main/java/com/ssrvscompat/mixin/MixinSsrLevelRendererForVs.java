package com.ssrvscompat.mixin;

import com.ssrvscompat.compat.ShoulderSurfingHelper;
import com.ssrvscompat.compat.ValkyrienSkiesHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lays SSR's user-configured shoulder shift on top of VS2's ship-mounted
 * camera, so the camera behaves like Epic Fight's TPS cam offset to a
 * shoulder - VS2's per-ship pullback distance and mouse-driven rotation are
 * preserved; we only add the lateral/vertical shoulder offset.
 *
 * <p><b>Background.</b> When the player is mounted on a VS2 ship in third
 * person, VS2's {@code MixinGameRenderer.setupCameraWithMountedShip} fires
 * inside its {@code @WrapOperation} on {@code LevelRenderer.prepareCullFrustum}.
 * Before invoking the wrapped method, VS2 calls
 * {@code MixinCamera.setupWithShipMounted}, which:
 * <ol>
 *   <li>Sets {@code Camera.position} to the ship-mounted player eye (transformed
 *       by ship rotation).</li>
 *   <li>Sets {@code Camera} rotation from {@code entity.getViewYRot/XRot}
 *       (player's mouse-driven view, ship-local) transformed by the ship's
 *       render rotation. This gives a camera that mouse-tracks correctly and
 *       banks with the ship.</li>
 *   <li>Pulls the camera back along ship-rotated forward by a ship-bbox-based
 *       distance ({@code (lenX+lenY+lenZ)/3 * 1.5}, min 4) to clear the ship's
 *       hull.</li>
 * </ol>
 * That's already a perfectly good third-person ship-mounted camera. The only
 * thing missing for a shoulder feel is the lateral/vertical shift.
 *
 * <p><b>What this mixin does.</b> HEAD inject inside the wrapped
 * {@code prepareCullFrustum} body - i.e. <em>after</em> VS2's setup. We:
 * <ul>
 *   <li>Read SSR's {@code targetOffset} (user-config-scale, e.g.
 *       {@code (0.75, 0, -0.75)}) - which is already processed through SSR's
 *       plugin callbacks, so Mod 2's OVERHEAD-mode override is honored.</li>
 *   <li>Call {@code Camera.move(0, target.y, target.x)} - applies the lateral
 *       shoulder shift and vertical offset using VS2's already-set ship-rotated
 *       basis vectors. {@code Z} (back distance) is intentionally dropped:
 *       VS2's ship-bbox pullback is already in effect and we don't want to
 *       stack SSR's pullback on top.</li>
 * </ul>
 *
 * <p>Position: untouched (VS2's correct ship-mounted-and-pulled-back position
 * stays). Rotation: untouched (VS2's mouse-tracking ship-rotated rotation
 * stays). Net effect: same as VS2's third-person ship cam, plus a shoulder
 * shift. Mouse turns continue to drive the camera. Ship banking still
 * propagates to the camera via VS2's ship-rotated basis. The shoulder shift
 * applies in the ship's frame, so the camera stays "off the right shoulder"
 * even when the ship rolls.
 *
 * <p>No-op unless VS2 is loaded, the player is mounted on a ship, the camera
 * is in third-person back view, and SSR is active.
 */
@Mixin(LevelRenderer.class)
public abstract class MixinSsrLevelRendererForVs {

    @Inject(method = "prepareCullFrustum", at = @At("HEAD"))
    private void ssrvscompat$applyShoulderShiftOnVsMount(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.options.getCameraType() != CameraType.THIRD_PERSON_BACK) return;
        if (!ShoulderSurfingHelper.isShoulderSurfingActive()) return;
        if (!ValkyrienSkiesHelper.isMountedOnShip(player)) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        if (camera == null) return;

        Vec3 target = ShoulderSurfingHelper.getTargetOffset();
        double effectiveX = target.x();
        double effectiveY = target.y();

        // SSR's "center camera when looking down" feature
        // (ShoulderSurfingCamera.calcOffset:270-273) replaces targetOffset with
        // (0, 0, z) whenever the camera's look angle gets within
        // centerCameraWhenLookingDownAngle of straight-down. Designed for
        // pillar-up off-ship - but on a ship, the player naturally pitches down
        // to see ahead along the hull, which constantly trips this and kills
        // the lateral shoulder shift along the ship's forward axis. (Hence the
        // "offset disappears facing forward, works facing sideways" symptom.)
        //
        // If SSR has collapsed the offset to (0, 0), fall back to the base
        // config X/Y to keep the shoulder shift active. OVERHEAD mode (handled
        // by Mod 2's ITargetCameraOffsetCallback) keeps Y > 0, so it falls
        // through this branch correctly.
        if (Math.abs(effectiveX) < 1.0E-4 && Math.abs(effectiveY) < 1.0E-4) {
            effectiveX = ShoulderSurfingHelper.getConfigOffsetX();
            effectiveY = ShoulderSurfingHelper.getConfigOffsetY();
        }

        if (Math.abs(effectiveX) < 1.0E-4 && Math.abs(effectiveY) < 1.0E-4) return;

        // Match SSR's MixinCamera.setupPosition arg convention: move(-z, y, x).
        // We pass z=0 so only the lateral and vertical shift apply (VS2's
        // ship-bbox pullback already covers back distance).
        ((CameraMoveInvoker) camera).ssrvscompat$callMove(0.0, effectiveY, effectiveX);
    }
}
