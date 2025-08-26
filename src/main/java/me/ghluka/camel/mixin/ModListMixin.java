package me.ghluka.camel.mixin;

import me.ghluka.camel.MainMod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static me.ghluka.camel.MainMod.mc;

@Mixin(value = FMLHandshakeMessage.ModList.class, remap = false)
public class ModListMixin {
    @Shadow
    private Map<String, String> modTags;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"))
    public void removeMods(List<ModContainer> modList, CallbackInfo ci) {
        if (mc.isIntegratedServerRunning())
            return;

        modTags.remove(MainMod.MODID);
    }
}