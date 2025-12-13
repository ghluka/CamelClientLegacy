package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import me.ghluka.camel.module.modules.hypixel.skyblock.PowderChestAura
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.util.*


class WTap : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Combo Tap"
        @Exclude
        const val CATEGORY = "Combat"
    }

    @Exclude
    @Info(text = "Automatically taps a key to assist combos.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Dropdown(name = "Combo Type", category = CATEGORY, subcategory = MODULE,
        options = ["W-Tap", "S-Tap", "Shift-Tap"])
    var comboType: Int = 0
    fun mlString(): String {
        return arrayOf("W-Tap", "S-Tap", "Shift-Tap")[comboType]
    }

    fun getCombo(): Int {
        if (comboing) {
            timer = 0L
            comboing = false
            finishCombo()
        }
        when (comboType) {
            1 -> {
                return mc.gameSettings.keyBindBack.keyCode
            }
            2 -> {
                return mc.gameSettings.keyBindSneak.keyCode
            }
        }
        return mc.gameSettings.keyBindForward.keyCode
    }
    
    @Switch(name = "Only target players", category = CATEGORY, subcategory = MODULE, size = 1)
    var onlyPlayers: Boolean = true
    @Slider(name = "Tap duration (ms)", category = CATEGORY, subcategory = MODULE, min = 1F, max = 500F, step = 0)
    var actionTicks: Float = 40F
    @Slider(name = "Randomizer (ms)", category = CATEGORY, subcategory = MODULE, min = 1F, max = 100F, step = 0)
    var randomTicks: Float = 30F
    @Slider(name = "Spacing", category = CATEGORY, subcategory = MODULE, min = 1F, max = 10F, step = 0)
    var onceEvery: Float = 1F
    @Slider(name = "Chance %", category = CATEGORY, subcategory = MODULE, min = 1F, max = 100F, step = 0)
    var chance: Float = 100F
    @Slider(name = "Range", category = CATEGORY, subcategory = MODULE, min = 1F, max = 6F, step = 0)
    var range: Float = 3F

    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    @Exclude
    var timer: Long = 0L
    @Exclude
    var comboing = false
    @Exclude
    var alreadyHit = false
    @Exclude
    var hitsWaited = 0

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

        val currentTime = System.currentTimeMillis()

        if (comboing) {
            if (currentTime >= timer) {
                comboing = false
                finishCombo()
            }
            return
        }

        val target = mc.objectMouseOver?.entityHit
        if (target !is Entity || !Mouse.isButtonDown(0)) return
        if (target.isDead) return
        if (mc.thePlayer.getDistanceToEntity(target) > range) return

        if (onlyPlayers && target !is EntityPlayer) return

        if (alreadyHit) {
            hitsWaited++
            if (hitsWaited >= onceEvery) {
                alreadyHit = false
                hitsWaited = 0
            } else {
                return
            }
        }

        val random = r.nextInt(100)
        if (random > chance) return

        if (!alreadyHit) {
            hitsWaited = 0

            startCombo()
            comboing = true
            var randomTick = (randomTicks / 2 - r.nextInt(randomTicks.toInt())).toLong()
            timer = currentTime + actionTicks.toLong() + randomTick
            alreadyHit = true
        }
    }

    private fun finishCombo() {
        var combo = getCombo()
        var wTapping = combo == mc.gameSettings.keyBindForward.keyCode

        if (wTapping) {
            if(Keyboard.isKeyDown(combo)) {
                KeyBinding.setKeyBindState(combo, true)
            }
        }
        else {
            if(!Keyboard.isKeyDown(combo)) {
                KeyBinding.setKeyBindState(combo, false)
            }
        }
    }

    private fun startCombo() {
        var combo = getCombo()
        var wTapping = combo == mc.gameSettings.keyBindForward.keyCode

        if (wTapping) {
            if(Keyboard.isKeyDown(combo)) {
                KeyBinding.setKeyBindState(combo, false)
                KeyBinding.onTick(combo)
            }
        }
        else {
            if(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode)) {
                KeyBinding.setKeyBindState(combo, true);
                KeyBinding.onTick(combo);
            }
        }
    }
}