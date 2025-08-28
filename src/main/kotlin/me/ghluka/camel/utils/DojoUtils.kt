package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.block.BlockStoneBrick
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos

class DojoUtils {
    val defaultSpawn = BlockPos(-207, 100, -598)
    var currentSpawn = defaultSpawn

    init {
        EventManager.INSTANCE.register(this)
    }

    @Subscribe
    fun onPacketReceived(event: ReceivePacketEvent) {
        if (event.packet is S08PacketPlayerPosLook && (mc.isSingleplayer || SkyblockUtils.hasLine("Dojo"))) {
            val tp = event.packet as S08PacketPlayerPosLook
            // test in singleplayer with /tp -206.5 100 -597.5
            currentSpawn = getCurrentSpawn(tp)?: defaultSpawn
            //println("dojo spawn is $currentSpawn, default is $defaultSpawn")
        }
    }

    fun getCurrentSpawn(tp: S08PacketPlayerPosLook): BlockPos? {
        if (mc.theWorld.getBlockState(BlockPos(tp.x, tp.y, tp.z).down()).block ==
            Blocks.stonebrick && mc.theWorld.getBlockState(BlockPos(tp.x, tp.y, tp.z).down()).getValue(BlockStoneBrick.VARIANT) ==
            BlockStoneBrick.EnumType.CHISELED) {
            return BlockPos(tp.x, tp.y, tp.z)
        }
        return null
    }
}