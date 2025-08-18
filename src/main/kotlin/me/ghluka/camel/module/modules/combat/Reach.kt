package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.MovingObjectPosition


class Reach : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Reach"
        @Exclude
        const val CATEGORY = "Combat"
    }
    
    @Exclude
    @Info(text = "Increases your range in hitting people", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Reach range", category = CATEGORY, subcategory = MODULE, min = 3F, max = 6F)
    var maxRange: Float = 4F

    @Switch(name = "Hit through walls", category = CATEGORY, subcategory = MODULE, size = 2)
    var hitThroughWalls: Boolean = false
    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var expand: Double = 0.0

}