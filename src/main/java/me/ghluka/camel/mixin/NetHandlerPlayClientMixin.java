package me.ghluka.camel.mixin;

import kotlin.Pair;
import me.ghluka.camel.MainMod;
import me.ghluka.camel.module.modules.hypixel.arcade.HoleInTheWallAIO;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(net.minecraft.client.network.NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {
    /*
    Module: Hole In The Wall AIO
     */
    @Inject(method = "addToSendQueue", at = @At("HEAD"), cancellable = true)
    public void onSendPacket(Packet packet, CallbackInfo ci) {
        HoleInTheWallAIO hitw = (HoleInTheWallAIO) MainMod.moduleManager.getModuleByName(HoleInTheWallAIO.MODULE);
        Minecraft mc = Minecraft.getMinecraft();
        if (hitw == null) return;
        if (!hitw.getModuleEnabled()) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!hitw.getBlockWrongClicks()) return;
        if (packet instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging dig = (C07PacketPlayerDigging) packet;
            BlockPos pos = dig.getPosition();

            List<BlockPos> levers = hitw.findTwoClosestLevers();
            if (levers.isEmpty()) return;

            Pair<Integer, Iterable<BlockPos>> result = hitw.getIncoming();
            int z = result.getFirst();
            Iterable<BlockPos> incoming = result.getSecond();

            if (incoming == null) return;

            for (BlockPos incBlock : incoming) {
                if (((z != 1000 && incBlock.getY() == pos.getY() && incBlock.getX() == pos.getX()) ||
                        (incBlock.getY() == pos.getY() && incBlock.getZ() == pos.getZ())) &&
                        mc.theWorld.getBlockState(incBlock).getBlock() == Blocks.air) {
                    ci.cancel();
                }
            }
        }
    }
}
