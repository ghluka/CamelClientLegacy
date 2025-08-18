package me.ghluka.camel.module.config.pages

import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.item.ItemSword
import net.minecraft.potion.Potion
import org.lwjgl.input.Keyboard

open class DefaultCombatPage : DefaultBattlePage() {
    @Switch(name = "Only while targeting", size = 1)
    var onlyWhileTargeting: Boolean = false
    @Switch(name = "Only with sword", size = 1)
    open var onlyWithWeapon: Boolean = false

    override fun result(): Boolean {
        if (onlyWithWeapon && (mc.thePlayer.currentEquippedItem == null || mc.thePlayer.currentEquippedItem.item !is ItemSword)) return true
        if (onlyWhileTargeting && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) return true

        return super.result()
    }
}