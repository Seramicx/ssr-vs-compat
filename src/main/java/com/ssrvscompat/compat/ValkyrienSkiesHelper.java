package com.ssrvscompat.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

import java.lang.reflect.Method;

public final class ValkyrienSkiesHelper {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static Method getShipMountedToMethod = null;
    private static boolean resolved = false;
    private static boolean resolvedOk = false;

    private ValkyrienSkiesHelper() {}

    private static void resolve() {
        if (resolved) return;
        resolved = true;
        try {
            Class<?> gameUtilsKt = Class.forName("org.valkyrienskies.mod.common.VSGameUtilsKt");
            getShipMountedToMethod = gameUtilsKt.getMethod("getShipMountedTo", Entity.class);
            resolvedOk = true;
        } catch (Throwable t) {
            LOGGER.warn("VS2 reflection failed, ssrvscompat is now a no-op: {}", t.getMessage());
        }
    }

    public static boolean isMountedOnShip(Entity entity) {
        if (entity == null) return false;
        resolve();
        if (!resolvedOk) return false;
        try {
            return getShipMountedToMethod.invoke(null, entity) != null;
        } catch (Throwable t) {
            return false;
        }
    }
}
