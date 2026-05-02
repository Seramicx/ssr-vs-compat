package com.ssrvscompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
