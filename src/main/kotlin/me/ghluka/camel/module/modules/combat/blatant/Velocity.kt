package me.ghluka.camel.module.modules.combat.blatant

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Page
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.PageLocation
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.Random

class Velocity : Module(MODULE) {
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

    @Slider(name = "Chance %", category = CATEGORY, subcategory = MODULE, min = 1F, max = 100F, step = 0)
    var chanceKb: Float = 100F
    @Slider(name = "Horizontal knockback", category = CATEGORY, subcategory = MODULE, min = 0F, max = 100F, step = 0)
    var horizontalKb: Float = 80F
    @Slider(name = "Vertical knockback", category = CATEGORY, subcategory = MODULE, min = 0F, max = 100F, step = 0)
    var verticalKb: Float = 100F
    fun mlString(): String {
        return "${horizontalKb.toInt()}% ${verticalKb.toInt()}%"
    }

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