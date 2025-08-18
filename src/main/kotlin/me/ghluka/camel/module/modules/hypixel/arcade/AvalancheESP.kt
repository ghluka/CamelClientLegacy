package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color


class AvalancheESP : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Avalanche ESP"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    @Info(text = "Renders the safe spots from snowball for the game Party Games (/play party_games).", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @cc.polyfrost.oneconfig.config.annotations.Color(name = "ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var espColor: OneColor = OneColor(Color.green)

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer != null && mc.theWorld != null) {
            for (x in -2405..-1892) {
                for (z in -2381..-1868) {
                    if (mc.theWorld.getBlockState(BlockPos(x, 49, z)).block === Blocks.wooden_slab) {
                        RenderUtils.re(BlockPos(x, 45, z), espColor.rgb)
                    }
                }
            }
        }
    }
}