package me.ghluka.camel.module.modules.movement

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraft.client.settings.KeyBinding
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent

class Sprint : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Sprint"
        @Exclude
        const val CATEGORY = "Movement"
    }

    @Exclude
    @Info(text = "Makes you always sprint.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false
    @Exclude
    @Info(text = "Warning: Omnisprint will ban you on Hypixel don't enable it on most servers.", subcategory = MODULE, category = CATEGORY, type = InfoType.ERROR, size = 2)
    var banWarning: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = true
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Switch(name = "Omnisprint", category = CATEGORY, subcategory = MODULE, size = 1)
    var omniSprint: Boolean = false

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (omniSprint) {
            if (!(mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F) &&
                mc.thePlayer.getFoodStats().foodLevel > 6) {
                mc.thePlayer.isSprinting = true;
            }
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, true);
        }
    }
}