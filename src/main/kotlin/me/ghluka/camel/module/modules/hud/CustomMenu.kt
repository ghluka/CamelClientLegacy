package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.InfoType
import me.ghluka.camel.MainMod.mc
import me.ghluka.camel.module.Module
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

class CustomMenu : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Custom Menu"
        @Exclude
        const val CATEGORY = "HUD"
    }
    
    @Exclude
    @Info(text = "Enables custom menu graphics.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 2)
    override var moduleEnabled: Boolean = true

    @Color(name = "Accent Color", category = CATEGORY, subcategory = MODULE)
    var accent = OneColor(244, 225, 185)

    @Color(name = "Main Screen Color", category = CATEGORY, subcategory = MODULE)
    var wallpaper = OneColor(255, 166, 0)

    var lastServerIP = ""

    init {
        initialize()
    }

    @SubscribeEvent
    fun onServerJoined(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        if (!event.isLocal) {
            lastServerIP = mc.currentServerData?.serverIP ?: ""
        }
    }
}