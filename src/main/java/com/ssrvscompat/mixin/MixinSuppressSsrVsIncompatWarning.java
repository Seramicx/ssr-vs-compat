package com.ssrvscompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Suppresses SSR's "Mod shouldersurfing is incompatible with valkyrienskies"
 * loading-screen warning, since this compat mod is the whole reason both can
 * coexist.
 *
 * <p><b>Where the warning comes from.</b> SSR's
 * {@code ShoulderSurfingForge.clientSetup} (in
 * {@code com.github.exopandora.shouldersurfing.forge.ShoulderSurfingForge}) reads
 * its {@code incompatibleMods} list from its own {@code mods.toml}
 * ({@code [modproperties.shouldersurfing] incompatibleMods = ["betterthirdperson",
 * "customcameraview", "nimble", "valkyrienskies"]}), then iterates loaded mods
 * and adds a {@link net.minecraftforge.fml.ModLoadingWarning} for each loaded
 * incompatible. The relevant call:
 *
 * <pre>{@code
 * List<?> incompatibleModIds = (List<?>) modProperties.getOrDefault("incompatibleMods", Collections.emptyList());
 * }</pre>
 *
 * <p><b>What this mixin does.</b> Wraps that single
 * {@code Map.getOrDefault} call. When SSR asks for {@code incompatibleMods},
 * we return a filtered copy of the list with {@code "valkyrienskies"} removed.
 * SSR's subsequent stream filter never matches VS, so no warning is added -
 * but SSR's own {@code customcameraview}/{@code nimble}/{@code betterthirdperson}
 * checks still fire normally if those mods are installed (we deliberately
 * don't suppress those).
 *
 * <p>{@code @Pseudo} because the target class is in SSR (runtime classpath
 * only). {@code remap = false} because the class/method names are mod
 * code, not vanilla MC code that needs SRG remapping. {@code require = 0}
 * via the @WrapOperation default tolerance means the mod still loads if SSR
 * isn't installed or the call site changes in a future SSR release.
 */
@Pseudo
@Mixin(targets = "com.github.exopandora.shouldersurfing.forge.ShoulderSurfingForge", remap = false)
public abstract class MixinSuppressSsrVsIncompatWarning {

    @Redirect(
        method = "clientSetup",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false
        ),
        require = 0,
        remap = false
    )
    private Object ssrvscompat$filterIncompatibleMods(Map<Object, Object> instance, Object key, Object defaultValue) {
        Object result = instance.getOrDefault(key, defaultValue);
        if (!"incompatibleMods".equals(key) || !(result instanceof List<?> list)) {
            return result;
        }
        List<Object> filtered = new ArrayList<>(list);
        filtered.removeIf(id -> "valkyrienskies".equals(String.valueOf(id)));
        return filtered;
    }
}
