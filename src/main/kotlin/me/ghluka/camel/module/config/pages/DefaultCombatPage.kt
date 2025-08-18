package me.ghluka.camel.module.config.pages

import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.item.ItemSword
import net.minecraft.potion.Potion
import org.lwjgl.input.Keyboard

open class DefaultCombatPage {
    @Switch(name = "Only with sword", size = 1)
    var onlyWithWeapon: Boolean = false
    @Switch(name = "Only while targeting", size = 1)
    var onlyWhileTargeting: Boolean = false
    @Switch(name = "Only on ground", size = 1)
    var onlyOnGround: Boolean = false
    @Switch(name = "Only while moving", size = 1)
    var onlyWhileMoving: Boolean = false
    @Switch(name = "Only while sprinting", size = 1)
    var onlyWhileSprinting: Boolean = false
    @Switch(name = "Only with speed", size = 1)
    var onlyWithSpeed: Boolean = false
    @Switch(name = "Disable while holding S", size = 1)
    var disableWhileS: Boolean = false

    fun result(): Boolean {
        if (onlyWithWeapon && (mc.thePlayer.currentEquippedItem == null || mc.thePlayer.currentEquippedItem.item !is ItemSword)) return true
        if (onlyWhileTargeting && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) return true
        if (onlyOnGround && !mc.thePlayer.onGround) return true
        if (onlyWhileMoving && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F) return true
        if (onlyWhileSprinting && !mc.thePlayer.isSprinting) return true
        if (onlyWithSpeed && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) return true
        if (disableWhileS && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode)) return true

        return false
    }
}