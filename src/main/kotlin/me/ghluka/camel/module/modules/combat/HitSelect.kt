package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class HitSelect : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude const val MODULE = "Hit Select"
        @Exclude const val CATEGORY = "Combat"
    }

    @Exclude
    @Info(text = "Chooses the best time to hit.", category = CATEGORY, subcategory = MODULE, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE)
    override var moduleEnabled: Boolean = false

    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Dropdown(
        name = "Mode", category = CATEGORY, subcategory = MODULE,
        options = ["Pause", "Active"]
    )
    var mode: Int = 0

    @Dropdown(
        name = "Preference", category = CATEGORY, subcategory = MODULE,
        options = ["Move speed", "KB reduction", "Critical hits"]
    )
    var preference: Int = 0

    @Slider(name = "Delay (ms)", category = CATEGORY, subcategory = MODULE, min = 300F, max = 500F, step = 1)
    var delay: Float = 420F

    @Slider(name = "Chance %", category = CATEGORY, subcategory = MODULE, min = 0F, max = 100F, step = 1)
    var chance: Float = 70F

    fun mlString(): String {
        return "${chance.toInt()}% ${arrayOf("Pause", "Active")[mode]}"
    }

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var attackTime: Long = -1L
    @Exclude
    var currentShouldAttack = false

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onAttack(event: AttackEntityEvent) {
        if (!moduleEnabled) return

        if (mode == 1 && !currentShouldAttack) {
            event.isCanceled = true
            return
        }

        if (canAttack()) {
            attackTime = System.currentTimeMillis()
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onTick(event: TickEvent.PlayerTickEvent) {
        if (!moduleEnabled) return
        if (event.player != mc.thePlayer) return

        currentShouldAttack = false

        if (Math.random() * 100 > chance) {
            currentShouldAttack = true
        } else {
            when (preference) {
                1 -> {
                    currentShouldAttack =
                        mc.thePlayer.hurtTime > 0 &&
                                !mc.thePlayer.onGround &&
                                (mc.thePlayer.moveForward != 0f || mc.thePlayer.moveStrafing != 0f)
                }

                2 -> {
                    currentShouldAttack =
                        !mc.thePlayer.onGround &&
                                mc.thePlayer.motionY < 0
                }
            }

            if (!currentShouldAttack) {
                currentShouldAttack =
                    System.currentTimeMillis() - attackTime >= delay.toLong()
            }
        }
    }

    fun canAttack(): Boolean {
        return canSwing()
    }

    fun canSwing(): Boolean {
        if (!moduleEnabled || mode == 1) return true
        return currentShouldAttack
    }
}
