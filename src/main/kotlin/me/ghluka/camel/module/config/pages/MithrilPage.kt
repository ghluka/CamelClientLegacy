package me.ghluka.camel.module.config.pages

import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

class MithrilPage {
    @Switch(name = "Light Blue Mithril", size = 1)
    var blue: Boolean = true
    @Switch(name = "Prismarine Mithril", size = 1)
    var prismarine: Boolean = true
    @Switch(name = "Gray Mithril", size = 1)
    var gray: Boolean = false
    @Switch(name = "Titanium", size = 1)
    var titanium: Boolean = true

    fun result(pos: BlockPos): Boolean {
        val state = MainMod.mc.theWorld.getBlockState(pos)
        val block = state.block
        val meta = block.getMetaFromState(state)

        val mithril = when (block) {
            Blocks.wool -> (meta == 3 && blue)
                    || (meta == 7 && gray)
            Blocks.stained_hardened_clay -> meta == 9 && gray
            Blocks.prismarine -> prismarine
            Blocks.stone -> meta == 4 && titanium
            else -> false
        }

        println("$pos, $mithril. $block")
        return mithril
    }
}