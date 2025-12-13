package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.events.event.SendPacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.Item
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*


class AutoBlock : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Auto Block"
        @Exclude
        const val CATEGORY = "Combat"
    }

    @Exclude
    @Info(text = "Automatically blocks with your sword when you click.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Block duration (ms)", category = CATEGORY, subcategory = MODULE, min = 1F, max = 500F, step = 0)
    var actionTicks: Float = 60F
    @Slider(name = "Randomizer (ms)", category = CATEGORY, subcategory = MODULE, min = 1F, max = 100F, step = 0)
    var randomTicks: Float = 80F
    @Slider(name = "Chance %", category = CATEGORY, subcategory = MODULE, min = 1F, max = 100F, step = 0)
    var chance: Float = 65F
    fun mlString(): String {
        return "${chance.toInt()}%"
    }

    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    @Exclude
    var timer: Long = 0L
    @Exclude
    var comboing = false

    @Exclude
    val r: Random = Random()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (defaultCombatPage.result()) return
        if (comboing) {
            val currentTime = System.currentTimeMillis()

            if (currentTime >= timer) {
                comboing = false
                finishCombo()
            }
            return
        }
    }

    @Subscribe
    fun onSwing(event: SendPacketEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (defaultCombatPage.result()) return

        if (mc.thePlayer.currentEquippedItem == null) return
        val item: Item? = mc.thePlayer.currentEquippedItem.item
        if (item !is ItemSword) return

        if (event.packet is C0APacketAnimation) {
            val currentTime = System.currentTimeMillis()

            val random = r.nextInt(100)
            if (random > chance) return

            startCombo()
            comboing = true
            var randomTick = (randomTicks / 2 - r.nextInt(randomTicks.toInt())).toLong()
            timer = currentTime + actionTicks.toLong() + randomTick
        }
    }

    private fun finishCombo() {
        val key = mc.gameSettings.keyBindUseItem.keyCode
        KeyBinding.setKeyBindState(key, false)
    }

    private fun startCombo() {
        val key = mc.gameSettings.keyBindUseItem.keyCode
        KeyBinding.setKeyBindState(key, true)
        KeyBinding.onTick(key)
    }
}