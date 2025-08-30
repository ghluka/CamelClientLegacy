package me.ghluka.camel.module.modules.movement

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.events.event.TickEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraft.client.settings.KeyBinding
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.cos
import kotlin.math.sin

class Parkour : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Parkour"
        @Exclude
        const val CATEGORY = "Movement"
    }

    @Exclude
    @Info(text = "Automatically jump off ledges!", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false
    @Exclude
    @Info(text = "Do not attempt in real life. Performed by trained professionals under controlled conditions.", subcategory = MODULE, category = CATEGORY, type = InfoType.WARNING, size = 2)
    var info2: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var debounce = false
    @Exclude
    var jumping = false

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (mc.thePlayer == null || mc.theWorld == null || !moduleEnabled) return
        if (debounce) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, jumping)
            debounce = false
        }
        if(mc.thePlayer.onGround && !mc.thePlayer.isSneaking
            && mc.theWorld
                .getCollisionBoxes(mc.thePlayer.entityBoundingBox
                    .offset(0.0, -0.5, 0.0).contract(0.001, 0.0, 0.001))
                .isEmpty()) {
            jumping = mc.gameSettings.keyBindJump.isKeyDown
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, true)
            debounce = true
        }
    }
}