package me.ghluka.camel.mixin;

import com.mojang.authlib.GameProfile;
import me.ghluka.camel.MainMod;
import me.ghluka.camel.module.modules.movement.SafeWalk;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import me.ghluka.camel.events.PlayerMoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.ghluka.camel.MainMod.mc;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin extends EntityPlayer {

    public EntityPlayerSPMixin(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalking(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PlayerMoveEvent.Pre())) ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void onWalking(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PlayerMoveEvent.Post())) ci.cancel();
    }

    @Override
    public boolean isSpectator() {
        return false;
    }
}