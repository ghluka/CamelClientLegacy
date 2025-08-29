package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class ServerLookUtils {
    var perspectiveEnabled: Boolean = false
        set(value) {
            if (value == perspectiveEnabled)
                return
            cameraPitch = mc.thePlayer.rotationPitch
            cameraYaw = mc.thePlayer.rotationYaw + 180f
            field = value
        }

    var cameraPitch: Float = 0f
    var cameraYaw: Float = 0f

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun cameraSetup(event: CameraSetup) {
        if (this.perspectiveEnabled) {
            event.pitch = this.cameraPitch
            event.yaw = this.cameraYaw
        }
    }
}