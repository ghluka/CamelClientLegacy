package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.utils.PlayerUtils
import me.ghluka.camel.utils.RotationUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class AutoJax : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Auto Jax"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = "Use any shortbow. Automatically aims and shoots at the targets in Jax's target practice", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()


    @Exclude
    private val rotations: Array<RotationUtils.Rotation> = arrayOf<RotationUtils.Rotation>(
            RotationUtils.Rotation(2.4f, 4.4f),
            RotationUtils.Rotation(-2.5f, -5.1f),
            RotationUtils.Rotation(-2.5f, -60f),
            RotationUtils.Rotation(6.6f, -91.8f),
            RotationUtils.Rotation(10.5f, -120.3f),
            RotationUtils.Rotation(-2.4f, -149.5f),
            RotationUtils.Rotation(-2.8f, -178.8f),
            RotationUtils.Rotation(8.3f, 173.9f),
            RotationUtils.Rotation(-1.5f, 153.8f),
            RotationUtils.Rotation(10.7f, 122.3f),
            RotationUtils.Rotation(-5.7f, 116.6f),
            RotationUtils.Rotation(4.5f, 99.9f),
            RotationUtils.Rotation(-2.2f, 90.8f),
            RotationUtils.Rotation(2.0f, 66.9f),
            RotationUtils.Rotation(-7.8f, 60.2f),
            RotationUtils.Rotation(-28.2f, 44.1f)
    )
    @Exclude
    private var rotating = false
    @Exclude
    private var currentIndex = -1

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    override fun save() {
        if (moduleEnabled) {
            currentIndex = -1
            rotating = false
        }
        super.save()
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.PlayerTickEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (MainMod.rotationUtils.done === false || rotating) return
        rotating = true
        if (currentIndex != -1)
            PlayerUtils.leftClick()
        currentIndex = currentIndex + 1
        if (currentIndex >= rotations.size) {
            moduleEnabled = false
            enabled = false
            currentIndex = -1
            rotating = false
            save()
            return
        }
        MainMod.rotationUtils.smoothLook(rotations[currentIndex], 500)
        rotating = false
    }
}