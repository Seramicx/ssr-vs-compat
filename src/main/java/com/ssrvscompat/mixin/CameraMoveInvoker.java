package com.ssrvscompat.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraMoveInvoker {
    @Invoker("move")
    void ssrvscompat$callMove(double distanceOffset, double verticalOffset, double lateralOffset);

    @Invoker("setPosition")
    void ssrvscompat$callSetPosition(Vec3 pos);

    @Invoker("setRotation")
    void ssrvscompat$callSetRotation(float yRot, float xRot);
}
