package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.clamp
import me.ghluka.camel.MainMod.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.network.play.server.S19PacketEntityStatus
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import java.util.Random


class JumpReset : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Jump Reset"
        @Exclude
        const val CATEGORY = "Combat"
    }

    @Exclude
    @Info(text = "A legit way to take less knockback.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Chance %", category = CATEGORY, subcategory = MODULE, min = 1f, max = 100f)
    var chance: Float = 100f

    @Slider(name = "Randomizer %", category = CATEGORY, subcategory = MODULE, min = 0f, max = 30f)
    var randomizer: Float = 5f

    @Slider(name = "Hit delay (ms)", category = CATEGORY, subcategory = MODULE, min = 0f, max = 20f)
    var hitDelay: Float = 4f

    @Slider(name = "Jump time (ticks)", category = CATEGORY, subcategory = MODULE, min = 1f, max = 10f)
    var jumpTicksBase: Float = 2f

    @Slider(name = "Release time (ticks)", category = CATEGORY, subcategory = MODULE, min = 1f, max = 10f)
    var unjumpTicksBase: Float = 3f

    @Switch(name = "Liquid Check", category = CATEGORY, subcategory = MODULE, size = 1)
    var disableInLiquid: Boolean = true

    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    @Exclude
    private val random = Random()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    private enum class Phase { IDLE, WAITING, JUMPING, RELEASE }
    @Exclude
    private var phase = Phase.IDLE

    @Exclude
    private var waitUntilMs: Long = 0
    @Exclude
    private var jumpingTicks = 0
    @Exclude
    private var releaseTicks = 0

    private fun randomized(base: Float): Int {
        return (base.toInt() + random.nextInt((randomizer * 2).toInt()) - randomizer.toInt()).coerceAtLeast(1)
    }

    @Subscribe
    fun onPacket(event: ReceivePacketEvent) {
        if (!moduleEnabled) return
        if (event.packet !is S19PacketEntityStatus) return

        if (disableInLiquid && (mc.thePlayer.isInWater || mc.thePlayer.isInLava)) return
        if (defaultCombatPage.result()) return

        val packet = event.packet as S19PacketEntityStatus
        val entity: Entity = packet.getEntity(mc.theWorld) ?: return

        if (packet.opCode.toInt() == 2 && entity === mc.thePlayer) {

            val roll = random.nextInt(100)
            if (roll > chance) return

            val delayMs = randomized(hitDelay)
            waitUntilMs = System.currentTimeMillis() + delayMs

            phase = Phase.WAITING
        }
    }

    @SubscribeEvent
    fun onTick(event: PlayerTickEvent?) {
        if (!moduleEnabled) return
        val player = mc.thePlayer ?: return

        when (phase) {

            Phase.WAITING -> {
                if (System.currentTimeMillis() >= waitUntilMs) {
                    jumpingTicks = randomized(jumpTicksBase)
                    phase = Phase.JUMPING
                }
            }

            Phase.JUMPING -> {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, true)
                phase = Phase.RELEASE
            }

            Phase.RELEASE -> {
                releaseTicks--
                if (releaseTicks <= 0) {
                    phase = Phase.IDLE
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, false)
                    releaseTicks = randomized(unjumpTicksBase)
                }
            }

            else -> {}
        }
    }
}