package me.ghluka.camel.mixin;

import me.ghluka.camel.MainMod;
import me.ghluka.camel.module.modules.movement.SafeWalk;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.ghluka.camel.MainMod.mc;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /*
    Module: Safe Walk
     */
    @Redirect(method = "moveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean onSafeWalk(@NotNull Entity instance) {
        if (instance == mc.thePlayer) {
            if (instance.onGround) {
                SafeWalk safeWalk = (SafeWalk) MainMod.moduleManager.getModuleByName("Safe Walk");
                if (safeWalk != null && safeWalk.getModuleEnabled() && safeWalk.getSafeWalking()) {
                    return true;
                }
            }
        }
        return instance.isSneaking();
    }
}
