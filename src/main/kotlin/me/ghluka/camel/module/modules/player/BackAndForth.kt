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


class BackAndForth : me.ghluka.camel.module.Module("Back and Forth") {
    @Exclude
    @Info(text = "Alternates strafing between A and D when you hit a wall, useful for farming on Hypixel Skyblock.", subcategory = "Back and Forth", category = "Player", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Back and Forth", category = "Player", subcategory = "Back and Forth", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Player", subcategory = "Back and Forth", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Page(category = "Player", subcategory = "Back and Forth", name = "Macro filters", location = PageLocation.BOTTOM)
    var defaultMacroPage: DefaultMacroPage = DefaultMacroPage()

    @Exclude
    var keyBinding: KeyBindingUtils = KeyBindingUtils()
    
    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onLivingUpdate(e: LivingEvent.LivingUpdateEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null) return
        if (!mc.thePlayer.onGround) return
        if (defaultMacroPage.result()) return

        var right = mc.gameSettings.keyBindRight.keyCode
        var left = mc.gameSettings.keyBindLeft.keyCode

        if ((facingX() && mc.thePlayer.motionZ == 0.0) || (!facingX() && mc.thePlayer.motionX == 0.0)) {
            if (mc.thePlayer.moveStrafing < 0) {
                keyBinding.setKeyBindState(right, false)
                KeyBinding.onTick(right)
                keyBinding.setKeyBindState(left, true)
                KeyBinding.onTick(left)
            }
            else if (mc.thePlayer.moveStrafing > 0) {
                keyBinding.setKeyBindState(right, true)
                KeyBinding.onTick(right)
                keyBinding.setKeyBindState(left, false)
                KeyBinding.onTick(left)
            }
        }
    }

    fun facingX(): Boolean {
        if (mc.thePlayer.rotationYaw in -165f..-15f || mc.thePlayer.rotationYaw in 15f..165f)
            return true
        return false
    }
}