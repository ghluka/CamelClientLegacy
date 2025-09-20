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
            try {
                cameraPitch = mc.thePlayer.rotationPitch
                cameraYaw = mc.thePlayer.rotationYaw + 180f
            }
            catch (_ : NullPointerException) {} // :(
            field = value
        }
    var cameraLock: Boolean = false

    var cameraPitch: Float = 0f
        set(value) {
            if (!cameraLock)
                field = value
        }
    var cameraYaw: Float = 0f
        set(value) {
            if (!cameraLock)
                field = value
        }

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