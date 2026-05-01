package com.ssrvscompat.compat;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.world.phys.Vec3;

/**
 * Direct facade over Shoulder Surfing Reloaded's API. SSR is a hard runtime
 * dependency for this mod (see mods.toml), so the imports are safe - the mod
 * loader refuses to start without it.
 *
 * <p>Each access is wrapped in try/catch to keep this mod resilient to
 * upstream API churn - a method disappearing returns a benign default
 * rather than crashing the camera.
 */
public final class ShoulderSurfingHelper {

    private ShoulderSurfingHelper() {}

    public static boolean isShoulderSurfingActive() {
        try {
            return ShoulderSurfing.getInstance().isShoulderSurfing();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * @return SSR's per-frame rendered offset (smoothed, lerped, distance-clipped).
     *         Convention matches SSR's own {@code MixinCamera.setupPosition}: the
     *         caller must apply via {@code Camera.move(-z, y, x)}.
     *         Returns {@link Vec3#ZERO} on any error.
     */
    public static Vec3 getRenderOffset() {
        try {
            IShoulderSurfingCamera cam = ShoulderSurfing.getInstance().getCamera();
            if (cam == null) return Vec3.ZERO;
            Vec3 ro = cam.getRenderOffset();
            return ro == null ? Vec3.ZERO : ro;
        } catch (Throwable t) {
            return Vec3.ZERO;
        }
    }

    /**
     * @return SSR's per-tick target offset in user-config scale (small shoulder
     *         shift values, e.g. {@code (0.75, 0, -0.75)}). Already post-processed
     *         by SSR plugin callbacks (so Mod 2's OVERHEAD override applies).
     *         Distinct from {@link #getRenderOffset()} which is normalized and
     *         distance-scaled - this one keeps the user's literal X/Y/Z config
     *         values and is what we want for laying a shoulder shift on top of
     *         someone else's camera (e.g. VS2's ship-mounted pullback).
     *         Returns {@link Vec3#ZERO} on error.
     */
    public static Vec3 getTargetOffset() {
        try {
            IShoulderSurfingCamera cam = ShoulderSurfing.getInstance().getCamera();
            if (cam == null) return Vec3.ZERO;
            Vec3 to = cam.getTargetOffset();
            return to == null ? Vec3.ZERO : to;
        } catch (Throwable t) {
            return Vec3.ZERO;
        }
    }

    /** SSR's tracked camera yaw (mouse-driven). NaN on error. */
    public static float getCameraYRot() {
        try {
            IShoulderSurfingCamera cam = ShoulderSurfing.getInstance().getCamera();
            if (cam == null) return Float.NaN;
            return cam.getYRot();
        } catch (Throwable t) {
            return Float.NaN;
        }
    }

    /** SSR's tracked camera pitch (mouse-driven). NaN on error. */
    public static float getCameraXRot() {
        try {
            IShoulderSurfingCamera cam = ShoulderSurfing.getInstance().getCamera();
            if (cam == null) return Float.NaN;
            return cam.getXRot();
        } catch (Throwable t) {
            return Float.NaN;
        }
    }

    /** SSR config offset_x (the un-modified base value, ignoring runtime modifiers/centering). 0 on error. */
    public static double getConfigOffsetX() {
        try {
            return ShoulderSurfing.getInstance().getClientConfig().getOffsetX();
        } catch (Throwable t) {
            return 0.0;
        }
    }

    /** SSR config offset_y (the un-modified base value, ignoring runtime modifiers/centering). 0 on error. */
    public static double getConfigOffsetY() {
        try {
            return ShoulderSurfing.getInstance().getClientConfig().getOffsetY();
        } catch (Throwable t) {
            return 0.0;
        }
    }
}
