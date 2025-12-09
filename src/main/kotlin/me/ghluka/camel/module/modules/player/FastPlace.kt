package me.ghluka.camel.module.modules.player

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class FastPlace : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Fast Place"
        @Exclude
        const val CATEGORY = "Player"
        @Exclude
        const val DESCRIPTION = "Removes the limit of how fast you can place blocks."
    }

    @Exclude
    @Info(text = DESCRIPTION, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE)
    override var moduleEnabled = false

    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE)
    var moduleKeyBind = OneKeyBind()

    @Slider(
        name = "Delay (ticks)", category = CATEGORY, subcategory = MODULE,
        min = 0f, max = 4f
    )
    var ticksDelay = 0f

    @Switch(name = "Blocks only", category = CATEGORY, subcategory = MODULE, size = 1)
    var onlyBlocks: Boolean = true
    @Switch(name = "Wool only", category = CATEGORY, subcategory = MODULE, size = 1)
    var onlyWool = false

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return

        if (onlyBlocks && mc.thePlayer.heldItem.item !is ItemBlock) {
            return
        }
        if (onlyWool && (mc.thePlayer.heldItem.item !is ItemBlock ||
                    (mc.thePlayer.heldItem.item as ItemBlock).block != Blocks.wool)) {
            return
        }

        try {
            val field = mc.javaClass.getDeclaredField("field_71467_ac")
            field.setAccessible(true)
            field.set(mc, ticksDelay.toInt())
        } catch (_: Exception) {
            try {
                val field = mc.javaClass.getDeclaredField("rightClickDelayTimer")
                field.setAccessible(true)
                field.set(mc, ticksDelay.toInt())
            } catch (_: Exception) {}
        }
    }
}
