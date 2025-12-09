package me.ghluka.camel.module.modules.hypixel.bedwars

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RotationUtils
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.abs

class BridgeAssist : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Bridge Assist"
        @Exclude
        const val CATEGORY = "Hypixel Bedwars"
        @Exclude
        const val DESCRIPTION = "Automatically adjusts your aim while bridging."
    }

    @Exclude
    @Info(text = DESCRIPTION, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE)
    override var moduleEnabled = false

    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(
        name = "Wait time (ms)", category = CATEGORY, subcategory = MODULE,
        min = 0f, max = 5000f
    )
    var waitTimeMs = 500f

    @Slider(
        name = "Assist range (deg)", category = CATEGORY, subcategory = MODULE,
        min = 1f, max = 40f
    )
    var assistRangeDeg = 10f

    @Dropdown(
        name = "Rotation Type", category = CATEGORY, subcategory = MODULE,
        options = ["Godbridge", "Moonwalk", "Breezily", "Normal"]
    )
    var modeIndex = 0

    @Switch(name = "Only when sneaking", category = CATEGORY, subcategory = MODULE)
    var onlyOnSneak = true

    private enum class State { IDLE, WAITING, ROTATING }

    private data class Mode(
        val name: String,
        val pitchPositions: FloatArray,
        val yawPositions: FloatArray
    )

    @Exclude
    private val modes = arrayOf(
        Mode(
            "Godbridge",
            pitchPositions = floatArrayOf(75.6f),
            yawPositions = floatArrayOf(-315f, -225f, -135f, -45f, 0f, 45f, 135f, 225f, 315f)
        ),
        Mode(
            "Moonwalk",
            pitchPositions = floatArrayOf(79.6f),
            yawPositions = floatArrayOf(-340f, -290f, -250f, -200f, -160f, -110f, -70f, -20f, 0f, 20f, 70f, 110f, 160f, 200f, 250f, 290f, 340f)
        ),
        Mode(
            "Breezily",
            pitchPositions = floatArrayOf(79.9f),
            yawPositions = floatArrayOf(-360f, -270f, -180f, -90f, 0f, 90f, 180f, 270f, 360f)
        ),
        Mode(
            "Normal",
            pitchPositions = floatArrayOf(78f),
            yawPositions = floatArrayOf(-315f, -225f, -135f, -45f, 0f, 45f, 135f, 225f, 315f)
        )
    )

    @Exclude
    private var state = State.IDLE
    @Exclude
    private var waitStart = 0L

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    private fun normalize(angle: Float): Float {
        return MathHelper.wrapAngleTo180_float(angle)
    }

    private fun angleDist(a: Float, b: Float): Float {
        return abs(normalize(a - b))
    }

    @SubscribeEvent
    fun onRenderTick(e: TickEvent.RenderTickEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return

        val pos = BlockPos(
            MathHelper.floor_double(mc.thePlayer.posX),
            MathHelper.floor_double(mc.thePlayer.posY - 1),
            MathHelper.floor_double(mc.thePlayer.posZ)
        )

        if (!(mc.theWorld.isAirBlock(pos) && mc.thePlayer.onGround)) {
            state = State.IDLE
            return
        }

        if (onlyOnSneak && !mc.thePlayer.isSneaking) {
            state = State.IDLE
            return
        }

        when (state) {
            State.IDLE -> {
                state = State.WAITING
                waitStart = System.currentTimeMillis()
                return
            }

            State.WAITING -> {
                val elapsed = System.currentTimeMillis() - waitStart
                if (elapsed < waitTimeMs) return
                state = State.ROTATING
            }

            State.ROTATING -> {
                val yaw = normalize(mc.thePlayer.rotationYaw)
                val pitch = normalize(mc.thePlayer.rotationPitch)

                val mode = modes[modeIndex]
                val range = assistRangeDeg

                for (p in mode.pitchPositions) {
                    if (angleDist(p, pitch) <= range) {
                        for (y in mode.yawPositions) {
                            if (angleDist(y, yaw) <= range) {

                                val rot = RotationUtils.Rotation(p, y)
                                MainMod.rotationUtils.smoothLook(rot, 100)

                                state = State.IDLE
                                return
                            }
                        }
                    }
                }

                state = State.IDLE
            }
        }
    }
}
