package me.ghluka.camel.module.config.pages

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class DefaultMacroPage {
    @Exclude
    var emergencyShutOff: Boolean = false

    @Switch(name = "Disable while on bedrock", size = 1)
    var disableOnBedrock: Boolean = true
    @Switch(name = "Stop on block placed", size = 1)
    var stopOnBlockPlaced: Boolean = true
    @Switch(name = "Stop on S08PacketPlayerPosLook (TP and rotation)", size = 1)
    var stopOnS08PacketPlayerPosLook: Boolean = true
    @Switch(name = "Stop on world change", size = 1)
    var stopOnWorldChange: Boolean = true

    fun result(): Boolean {
        var pos = BlockPos(mc.thePlayer.positionVector).down(1)
        if (disableOnBedrock && mc.theWorld.getBlockState(pos).block == Blocks.bedrock &&
            disableOnBedrock && mc.theWorld.getBlockState(pos.up(1)).block != Blocks.carpet)
            return true

        return false
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
        EventManager.INSTANCE.register(this)
    }

    @Subscribe
    fun onPacketReceive(event: ReceivePacketEvent) {
        if (event.packet is S08PacketPlayerPosLook && stopOnS08PacketPlayerPosLook && mc.thePlayer.posY > 0)
            emergencyShutOff = true
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        if (stopOnWorldChange)
            emergencyShutOff = true
    }

    @SubscribeEvent
    fun onBlockPlaced(event: BlockEvent.NeighborNotifyEvent) {
        val pos = event.pos
        val block: Block = event.world.getBlockState(pos).block

        if (stopOnBlockPlaced && block.isFullBlock)
            emergencyShutOff = true
    }
}