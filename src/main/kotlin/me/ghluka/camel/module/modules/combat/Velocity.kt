package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*


class Velocity : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Velocity"
        @Exclude
        const val CATEGORY = "Combat"
    }

    @Exclude
    @Info(text = "Decreases your knockback", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Chance %", category = CATEGORY, subcategory = MODULE, min = 1F, max = 100F, step = 1)
    var chanceKb: Float = 100F
    @Slider(name = "Horizontal knockback", category = CATEGORY, subcategory = MODULE, min = 0F, max = 100F, step = 1)
    var horizontalKb: Float = 80F
    @Slider(name = "Vertical knockback", category = CATEGORY, subcategory = MODULE, min = 0F, max = 100F, step = 1)
    var verticalKb: Float = 100F

    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    @Exclude
    val r: Random = Random()

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
        if (defaultCombatPage.result()) return

        val random: Int = r.nextInt(100)
        if (random <= chanceKb && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime && mc.thePlayer.maxHurtTime > 0) {
            mc.thePlayer.motionX *= (horizontalKb / 100.0f).toDouble()
            mc.thePlayer.motionY *= (verticalKb / 100.0f).toDouble()
            mc.thePlayer.motionZ *= (horizontalKb / 100.0f).toDouble()
        }
    }
}