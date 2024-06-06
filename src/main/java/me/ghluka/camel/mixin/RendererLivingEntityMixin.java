package me.ghluka.camel.mixin;

import me.ghluka.camel.MainMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RendererLivingEntity.class)
public class RendererLivingEntityMixin {
    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private void onCanRenderName(CallbackInfoReturnable<Boolean> cir) {
        if (MainMod.INSTANCE.moduleManager.getTargetHud().getTargetHud().getRenderingNametag())
            cir.setReturnValue(false);
    }

    @Inject(method = "renderModel", at = @At("HEAD"))
    private void onRenderModel(EntityLivingBase entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor, CallbackInfo ci) {
        GlStateManager.depthMask(true);
    }
}