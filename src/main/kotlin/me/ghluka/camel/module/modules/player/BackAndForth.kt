package me.ghluka.camel.module.modules.player

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.config.pages.DefaultMacroPage
import me.ghluka.camel.utils.KeyBindingUtils
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class BackAndForth : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Back And Forth"
        @Exclude
        const val CATEGORY = "Player"
    }

    @Exclude
    @Info(text = "Alternates strafing between A and D when you hit a wall, useful for farming on Hypixel Skyblock.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultMacroPage: DefaultMacroPage = DefaultMacroPage()

    @Exclude
    var keyBinding: KeyBindingUtils = KeyBindingUtils()

    @Exclude
    var ticks: Int = 0
    
    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null) return
        if (!mc.thePlayer.onGround) return
        if (mc.currentScreen != null) return

        if (defaultMacroPage.emergencyShutOff) {
            moduleEnabled = false
            defaultMacroPage.emergencyShutOff = false
            return
        }
        if (defaultMacroPage.result()) return

        ticks++
        if (ticks > 10) ticks = 0
        if (ticks != 0) return

        var right = mc.gameSettings.keyBindRight.keyCode
        var left = mc.gameSettings.keyBindLeft.keyCode

        if ((mc.thePlayer.motionZ == 0.0 && facingX()) || (mc.thePlayer.motionX == 0.0 && !facingX())) {
            if (keyBinding.getKeyBindState(left)) {
                keyBinding.setKeyBindState(left, false)
                KeyBinding.onTick(left)
                keyBinding.setKeyBindState(right, true)
                KeyBinding.onTick(right)
            }
            else if (keyBinding.getKeyBindState(right)) {
                keyBinding.setKeyBindState(right, false)
                KeyBinding.onTick(right)
                keyBinding.setKeyBindState(left, true)
                KeyBinding.onTick(left)
            }
        }
    }

    fun facingX(): Boolean {
        var yaw: Float = mc.thePlayer.rotationYawHead
        yaw %= 360.0f
        if (yaw >= 180.0f) {
            yaw -= 360.0f
        }
        if (yaw < -180.0f) {
            yaw += 360.0f
        }

        return yaw in -135f..-45f || yaw in 45f..135f
    }
}