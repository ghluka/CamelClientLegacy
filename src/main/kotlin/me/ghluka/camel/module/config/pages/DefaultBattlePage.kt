package me.ghluka.camel.module.config.pages

import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.item.ItemSword
import net.minecraft.potion.Potion
import org.lwjgl.input.Keyboard

open class DefaultBattlePage {
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

    open fun result(): Boolean {
        if (onlyOnGround && !mc.thePlayer.onGround) return true
        if (onlyWhileMoving && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F) return true
        if (onlyWhileSprinting && !mc.thePlayer.isSprinting) return true
        if (onlyWithSpeed && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) return true
        if (disableWhileS && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode)) return true

        return false
    }
}