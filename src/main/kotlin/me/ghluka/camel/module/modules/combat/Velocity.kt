package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.command.commands.Camel
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemSword
import net.minecraft.potion.Potion
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*


class Velocity : me.ghluka.camel.module.Module("Velocity") {
    @Switch(
        name = "Enable velocity",
        category = "Combat",
        subcategory = "Velocity",
        size = 1
    )
    override var moduleEnabled: Boolean = false
    @KeyBind(
        name = "",
        category = "Combat",
        subcategory = "Velocity",
        size = 1
    )
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(
        name = "Chance %",
        category = "Combat",
        subcategory = "Velocity",
        min = 1F,
        max = 100F,
        step = 1
    )
    var chanceKb: Float = 100F
    @Slider(
        name = "Horizontal knockback",
        category = "Combat",
        subcategory = "Velocity",
        min = 1F,
        max = 100F,
        step = 1
    )
    var horizontalKb: Float = 100F
    @Slider(
        name = "Vertical knockback",
        category = "Combat",
        subcategory = "Velocity",
        min = 1F,
        max = 100F,
        step = 1
    )
    var verticalKb: Float = 100F

    val r: Random = Random()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
        save()
    }

    @SubscribeEvent
    fun onLivingUpdate(e: LivingEvent.LivingUpdateEvent?) {
        if (mc.thePlayer == null) return

        val random: Int = r.nextInt(100)
        if (random <= chanceKb && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime && mc.thePlayer.maxHurtTime > 0) {
            mc.thePlayer.motionX *= (horizontalKb / 100.0f).toDouble()
            mc.thePlayer.motionY *= (verticalKb / 100.0f).toDouble()
            mc.thePlayer.motionZ *= (horizontalKb / 100.0f).toDouble()
        }
    }
}