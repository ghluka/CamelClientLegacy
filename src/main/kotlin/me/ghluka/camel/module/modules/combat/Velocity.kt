package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import net.minecraft.item.ItemSword
import net.minecraft.potion.Potion
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import java.util.*


class Velocity : me.ghluka.camel.module.Module("Velocity") {
    @Exclude
    @Info(text = "Decreases your knockback", subcategory = "Velocity", category = "Combat", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable velocity", category = "Combat", subcategory = "Velocity", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Combat", subcategory = "Velocity", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Chance %", category = "Combat", subcategory = "Velocity", min = 1F, max = 100F, step = 1)
    var chanceKb: Float = 100F
    @Slider(name = "Horizontal knockback", category = "Combat", subcategory = "Velocity", min = 0F, max = 100F, step = 1)
    var horizontalKb: Float = 80F
    @Slider(name = "Vertical knockback", category = "Combat", subcategory = "Velocity", min = 0F, max = 100F, step = 1)
    var verticalKb: Float = 100F

    @Page(category = "Combat", subcategory = "Velocity", name = "Velocity filters", location = PageLocation.BOTTOM)
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

        if (defaultCombatPage.onlyWithWeapon && (mc.thePlayer.currentEquippedItem == null || mc.thePlayer.currentEquippedItem.item !is ItemSword)) return
        if (defaultCombatPage.onlyWhileTargeting && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) return
        if (defaultCombatPage.onlyOnGround && !mc.thePlayer.onGround) return
        if (defaultCombatPage.onlyWhileMoving && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F) return
        if (defaultCombatPage.onlyWhileSprinting && !mc.thePlayer.isSprinting) return
        if (defaultCombatPage.onlyWithSpeed && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) return
        if (defaultCombatPage.disableWhileS && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode)) return

        val random: Int = r.nextInt(100)
        if (random <= chanceKb && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime && mc.thePlayer.maxHurtTime > 0) {
            mc.thePlayer.motionX *= (horizontalKb / 100.0f).toDouble()
            mc.thePlayer.motionY *= (verticalKb / 100.0f).toDouble()
            mc.thePlayer.motionZ *= (horizontalKb / 100.0f).toDouble()
        }
    }
}