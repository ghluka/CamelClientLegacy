package me.ghluka.camel.module.modules.player

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.SendPacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.network.play.client.C07PacketPlayerDigging

class AutoTool : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Auto Tool"
        @Exclude
        const val CATEGORY = "Player"
    }

    @Exclude
    @Info(text = "Swaps to the right tool whenever you try to break a block.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

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

    @Subscribe
    fun onPacketSent(event: SendPacketEvent) {
        if (event.packet is C07PacketPlayerDigging) {
            val dig = event.packet as C07PacketPlayerDigging
            if (moduleEnabled && !mc.thePlayer.isUsingItem
                && dig.status == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                val block = mc.theWorld.getBlockState(dig.position).block

                PlayerUtils.pickItem { stack ->
                    stack?.getStrVsBlock(block)!! > (
                            if (mc.thePlayer.inventory.getCurrentItem() == null)
                                1.0f
                            else
                                mc.thePlayer.inventory.getCurrentItem().getStrVsBlock(block)
                            )
                }
            }
        }
    }
}