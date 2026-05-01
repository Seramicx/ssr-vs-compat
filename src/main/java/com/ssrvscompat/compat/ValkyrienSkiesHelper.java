package com.ssrvscompat.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Reflection wrapper for Valkyrien Skies 2. We only need
 * {@code VSGameUtilsKt.getShipMountedTo(Entity)} for the gate - if the entity
 * is mounted on a ship, our mixin re-applies the SSR offset.
 *
 * <p>Reflection-only so this mod can compile without VS2 on the classpath.
 * VS2 is still a hard runtime dep (mods.toml) - Forge will refuse to load
 * without it. The reflection just keeps build-time light.
 */
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
