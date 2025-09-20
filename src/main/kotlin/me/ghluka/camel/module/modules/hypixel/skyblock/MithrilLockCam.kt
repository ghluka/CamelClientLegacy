package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Page
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.PageLocation
import me.ghluka.camel.MainMod
import me.ghluka.camel.MainMod.mc
import me.ghluka.camel.module.config.pages.MithrilPage
import me.ghluka.camel.utils.RotationUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Mouse
import kotlin.math.abs

class MithrilLockCam : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Mithril Lockcam"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = "Lets you mine mithril while looking at another ore. Not recommended in Crystal Hollows.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Rotation Speed", category = CATEGORY, subcategory = MODULE, min = 50F, max = 500F, step = 0)
    var rotationSpeed: Float = 100F
    
    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var mithrilPage: MithrilPage = MithrilPage()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var rotatingBack = false

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!moduleEnabled) {
            return
        }
        if (rotatingBack) {
            try {
                val original = RotationUtils.Rotation(
                    MainMod.serverLookUtils.cameraPitch % 360,
                    MainMod.serverLookUtils.cameraYaw % 360 + 180f
                )
                MainMod.rotationUtils.smoothLook(original, rotationSpeed.toLong())
                val yawDiff = abs(original.yaw % 360 - mc.thePlayer.rotationYaw % 360)
                val pitchDiff = abs(original.pitch % 360 - mc.thePlayer.rotationPitch % 360)
                if ((yawDiff < .2 || yawDiff > 359.8) && (pitchDiff < .2 || pitchDiff > 359.8)) {
                    MainMod.serverLookUtils.perspectiveEnabled = false
                    rotatingBack = false
                }
            } catch (_: NullPointerException) {
                MainMod.serverLookUtils.perspectiveEnabled = false
                rotatingBack = false
            }
        }
        
        val hit = mc.objectMouseOver
        if (hit != null && hit.blockPos != null &&
            Mouse.isButtonDown(0) && mithrilPage.result(hit.blockPos)) {
            rotatingBack = false
            MainMod.serverLookUtils.perspectiveEnabled = true
        }
        else if (MainMod.serverLookUtils.perspectiveEnabled) rotatingBack = true
    }
}