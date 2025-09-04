package me.ghluka.camel.module.modules.world

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import me.ghluka.camel.module.Module

class PSTWaypoint : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Pizza to Skytils Waypoints"
        @Exclude
        const val CATEGORY = "World"
    }

    @Exclude
    @Info(text = "Adds Pizza Client's World Scanner detections to Skytils' Crystal Hollows waypoints.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Switch(name = "Add misc waypoints (e.g. throne pillar)", category = CATEGORY, subcategory = MODULE, size = 2)
    var miscWaypoints: Boolean = false

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    // see me.ghluka.camel.mixin.GuiNewChatMixin for the module code
}