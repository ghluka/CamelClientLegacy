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
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class NoFall : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "No Fall"
        @Exclude
        const val CATEGORY = "Movement"
    }

    @Exclude
    @Info(text = "Prevents you from taking fall damage! Might interfere with flight's vanilla anti-kick.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false
    @Exclude
    @Info(text = "Warning: Enable this in Hypixel and enjoy your ban vacation.", subcategory = MODULE, category = CATEGORY, type = InfoType.ERROR, size = 2)
    var banWarning: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Dropdown(name = "Prevention mode", options = ["Vanilla"], category = CATEGORY, subcategory = MODULE)
    var noFallMode: Int = 0

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (mc.thePlayer == null || mc.theWorld == null || !moduleEnabled) return
        when (noFallMode) {
            0 -> {
                if (mc.thePlayer.fallDistance > 2.5F) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                }
            }
        }
    }
}