package me.ghluka.camel.module.modules.render

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.modules.movement.Flight
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

class Perspective : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Perspective"
        @Exclude
        const val CATEGORY = "Render"
    }
    
    @Exclude
    @Info(text = "F5 but your camera doesn't rotate, so you can see around you while not showing it on the server.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @KeyBind(name = "Hold Keybind", category = CATEGORY, subcategory = MODULE, size = 2)
    var moduleKeyBind: OneKeyBind = OneKeyBind(UKeyboard.KEY_R)

    @Exclude
    private var wasHolding = false

    @Dropdown(name = "Perspective View", options = ["First Person", "Third Person", "Second Person"], category = CATEGORY, subcategory = MODULE)
    var perspectiveMode: Int = 1

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return
        }
        if (!moduleKeyBind.keyBinds.any { Keyboard.isKeyDown(it) }) {
            if (wasHolding) {
                MainMod.serverLookUtils.perspectiveEnabled = false
                mc.gameSettings.thirdPersonView = 0
            }
            wasHolding = false
            return
        }

        wasHolding = true
        MainMod.serverLookUtils.perspectiveEnabled = true
        mc.gameSettings.thirdPersonView = perspectiveMode
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindTogglePerspective.keyCode, false)
    }

    init {
        initialize()
    }
}