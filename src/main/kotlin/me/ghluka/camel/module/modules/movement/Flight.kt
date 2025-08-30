package me.ghluka.camel.module.modules.movement

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.events.event.TickEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.cos
import kotlin.math.sin

class Flight : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Flight"
        @Exclude
        const val CATEGORY = "Movement"
    }

    @Exclude
    @Info(text = "FTC flight team stand up!", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false
    @Exclude
    @Info(text = "Warning: Enable this in Hypixel and enjoy your ban vacation.", subcategory = MODULE, category = CATEGORY, type = InfoType.ERROR, size = 2)
    var banWarning: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
        set(value) {
            if (!value) {
                when (flightMode) {
                    0 -> {
                        mc.thePlayer.capabilities.isFlying = false
                    }
                    1 -> {
                        mc.thePlayer.capabilities.isFlying = false
                    }
                }
            }
            field = value
        }
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Dropdown(name = "Flight mode", options = ["Vanilla", "Creative"], category = CATEGORY, subcategory = MODULE)
    var flightMode: Int = 0

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (mc.thePlayer == null || mc.theWorld == null || !moduleEnabled) return
        when (flightMode) {
            0 -> {
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.capabilities.isFlying = true

                val player = mc.thePlayer
                var yaw = player.rotationYaw

                val forward = when {
                    player.moveForward < 0f -> (-0.5f).also { yaw += 180f }
                    player.moveForward > 0f -> 0.5f
                    else -> {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        return
                    }
                }

                yaw += when {
                    player.moveStrafing > 0f -> -90f * forward
                    player.moveStrafing < 0f -> 90f * forward
                    else -> 0f
                }
                mc.thePlayer.motionX = -sin(Math.toRadians(yaw.toDouble())) * 0.25
                mc.thePlayer.motionZ = cos(Math.toRadians(yaw.toDouble())) * 0.25
            }
            1 -> {
                mc.thePlayer.capabilities.isFlying = true
            }
        }
    }

    // anti-kick

    @Switch(name = "Vanilla Anti Kick", category = CATEGORY, subcategory = MODULE, size = 1)
    var antiKick = true
    @Slider(name = "Anti Kick Interval", category = CATEGORY, subcategory = MODULE, min = 5F, max = 80F, step = 1)
    var antiKickInterval: Float = 30F

    @Exclude
    var tickCounter = 0

    @Subscribe
    fun onTick(event: TickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || !moduleEnabled) return
        if (event.stage === Stage.START && antiKick) {
            wurstAntiKick()
        }
    }

    fun wurstAntiKick() { // lol
        if(tickCounter > antiKickInterval)
            tickCounter = 0

        when (tickCounter) {
            0 -> {
                goToGround()
            }
        }

        tickCounter++
    }

    fun goToGround() {
        var step = 1.0
        val precision = 0.0625
        var flyHeight = 0.0

        val box = mc.thePlayer.entityBoundingBox.expand(precision, precision, precision)

        while (flyHeight < mc.thePlayer.posY) {
            val nextBox = box.offset(0.0, -flyHeight, 0.0)

            if (mc.thePlayer.worldObj.checkBlockCollision(nextBox)) {
                if (step < precision) break
                flyHeight -= step
                step /= 2
            } else {
                flyHeight += step
            }
        }

        if (flyHeight > 300) return

        val minY: Double = mc.thePlayer.posY - flyHeight

        if (minY <= 0) return

        run {
            var y: Double = mc.thePlayer.posY
            while (y > minY) {
                y -= 8.0
                if (y < minY) y = minY

                val packet =
                    C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true)
                mc.netHandler.addToSendQueue(packet)
            }
        }

        var y = minY
        while (y < mc.thePlayer.posY) {
            y += 8.0
            if (y > mc.thePlayer.posY) y = mc.thePlayer.posY

            val packet =
                C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true)
            mc.netHandler.addToSendQueue(packet)
        }
    }
}