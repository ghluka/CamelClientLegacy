package me.ghluka.camel.module.config.pages

import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

class DefaultMacroPage {
    @Switch(name = "Disable while on bedrock", size = 1)
    var disableOnBedrock: Boolean = false

    fun result(): Boolean {
        if (disableOnBedrock && mc.theWorld.getBlockState(mc.thePlayer.position.down(-1) as BlockPos).block == Blocks.bedrock) return true

        return false
    }
}