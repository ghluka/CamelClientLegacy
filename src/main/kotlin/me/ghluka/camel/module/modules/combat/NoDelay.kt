package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent


class NoDelay : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "No Delay"
        @Exclude
        const val CATEGORY = "Combat"
    }
    
    @Exclude
    @Info(text = "Gives you 1.7 hit registration.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

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
        try {
            val field = mc.javaClass.getDeclaredField("field_71429_W")
            field.setAccessible(true)
            field.set(mc, 0)
        } catch (_: Exception) {
            try {
                val field = mc.javaClass.getDeclaredField("leftClickCounter")
                field.setAccessible(true)
                field.set(mc, 0)
            } catch (_: Exception) {}
        }
    }

}