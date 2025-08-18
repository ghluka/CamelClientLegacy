package me.ghluka.camel.module.modules

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import me.ghluka.camel.module.modules.combat.Reach
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.client.Minecraft
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.Random


class Template : Module(MODULE) {
    // don't forget to register your module in the ModuleManager or it won't work
    @Exclude
    companion object {
        @Exclude
        /*
         oneconfig tries to save everything in v0, make sure to Exclude any outer variables
         or it'll attempt to save it, which in some cases (constants, etc) will lead to crashes
         or corrupted config files.
        */
        const val MODULE = "Template" // module's name
        @Exclude
        const val DESCRIPTION = "Lorem ipsum dolor sit amet consectetur adipiscing elit." // module description

        @Exclude
        const val CATEGORY = "Combat"
        /*
            Categories:
         Combat, HUD, Hypixel Arcade, Hypixel Skyblock, Player, Render, Misc, Dev
        */
    }
    
    @Exclude
    @Info(text = DESCRIPTION, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    // you can add config options & pages here
    // learn more at https://docs.polyfrost.org/oneconfig/config/adding-options

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        // you can subscribe to various forge or oneconfig events to make your module do something
    }
}