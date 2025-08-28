package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import net.minecraft.block.BlockStoneBrick
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class DojoUtils {
    val defaultSpawn = BlockPos(-207, 100, -598)
    var currentSpawn = defaultSpawn

    var tp: S08PacketPlayerPosLook? = null

    init {
        EventManager.INSTANCE.register(this)
    }

    @Subscribe
    fun onPacketReceived(event: ReceivePacketEvent) {
        // test in singleplayer with /tp -206.5 100 -597.5
        if (event.packet is S08PacketPlayerPosLook && (mc.isSingleplayer || SkyblockUtils.hasLine("Dojo"))) {
            tp = event.packet as S08PacketPlayerPosLook
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (event.phase === TickEvent.Phase.START) return

        if (tp != null) {
            currentSpawn = getCurrentSpawn(tp!!)?: defaultSpawn

            tp = null
        }
    }

    fun getCurrentSpawn(tp: S08PacketPlayerPosLook): BlockPos? {
        val tpPos = BlockPos(tp.x, tp.y, tp.z)
        val state = mc.theWorld.getBlockState(tpPos.down())

        if (!mc.theWorld.isBlockLoaded(tpPos.down()))
            return null
        else if (state.block == Blocks.stonebrick &&
            state.getValue(BlockStoneBrick.VARIANT) == BlockStoneBrick.EnumType.CHISELED)
            return BlockPos(tp.x, tp.y, tp.z)

        return null
    }
}