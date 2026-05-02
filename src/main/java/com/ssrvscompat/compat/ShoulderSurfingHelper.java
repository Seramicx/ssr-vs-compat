package com.ssrvscompat.compat;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.world.phys.Vec3;

public final class ShoulderSurfingHelper {

    private ShoulderSurfingHelper() {}

    public static boolean isShoulderSurfingActive() {
        try {
            return ShoulderSurfing.getInstance().isShoulderSurfing();
        } catch (Throwable t) {
            return false;
        }
    }

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

    public static float getCameraYRot() {
        try {
            IShoulderSurfingCamera cam = ShoulderSurfing.getInstance().getCamera();
            if (cam == null) return Float.NaN;
            return cam.getYRot();
        } catch (Throwable t) {
            return Float.NaN;
        }
    }

    public static float getCameraXRot() {
        try {
            IShoulderSurfingCamera cam = ShoulderSurfing.getInstance().getCamera();
            if (cam == null) return Float.NaN;
            return cam.getXRot();
        } catch (Throwable t) {
            return Float.NaN;
        }
    }

    public static double getConfigOffsetX() {
        try {
            return ShoulderSurfing.getInstance().getClientConfig().getOffsetX();
        } catch (Throwable t) {
            return 0.0;
        }
    }

    public static double getConfigOffsetY() {
        try {
            return ShoulderSurfing.getInstance().getClientConfig().getOffsetY();
        } catch (Throwable t) {
            return 0.0;
        }
    }
}
