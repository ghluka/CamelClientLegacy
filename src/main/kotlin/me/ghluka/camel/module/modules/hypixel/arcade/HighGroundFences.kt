package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.command.commands.Camel
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class HighGroundFences : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "High Ground Fences"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    @Info(text = "Makes it so you can't fall off the map in High Ground", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable High Ground Fences", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false

    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()
    
    @Slider(name = "Fence Height", category = CATEGORY, subcategory = MODULE, min = 1F, max = 4F, step = 1)
    var fenceHeight: Float = 1F

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
            save()
        }
    }

    override fun save() {
        super.save()

        if (!moduleEnabled) return
        if (mc.theWorld == null) return

        for (pos in this.positions) {
            for (n in 0..3) {
                mc.theWorld.setBlockToAir(pos.up(n))
            }
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null) return
        if (mc.theWorld == null) return

        if (mc.thePlayer.posY > 41.0) {
            for (pos in positions) {
                mc.theWorld.setBlockState(pos, Blocks.oak_fence.defaultState)
                for (n in fenceHeight.toInt()..3) {
                    mc.theWorld.setBlockToAir(pos.up(n))
                }
                mc.theWorld.setBlockState(pos, Blocks.oak_fence.defaultState)
                if (fenceHeight > 1)
                    for (n in 1..fenceHeight.toInt()) {
                        mc.theWorld.setBlockState(pos.up(n), Blocks.oak_fence.defaultState)
                    }
            }
        } else {
            for (pos in this.positions) {
                for (n in 0..3) {
                    mc.theWorld.setBlockToAir(pos.up(n))
                }
            }
        }
    }

    @Exclude
    private val positions = arrayOf(BlockPos(393, 42, 734), BlockPos(393, 42, 733), BlockPos(393, 42, 732), BlockPos(393, 42, 731), BlockPos(393, 42, 730), BlockPos(379, 42, 734), BlockPos(379, 42, 733), BlockPos(379, 42, 732), BlockPos(379, 42, 731), BlockPos(379, 42, 730), BlockPos(384, 42, 725), BlockPos(385, 42, 725), BlockPos(386, 42, 725), BlockPos(387, 42, 725), BlockPos(388, 42, 725), BlockPos(384, 42, 739), BlockPos(385, 42, 739), BlockPos(386, 42, 739), BlockPos(387, 42, 739), BlockPos(388, 42, 739), BlockPos(391, 42, 737), BlockPos(381, 42, 737), BlockPos(391, 42, 727), BlockPos(381, 42, 727), BlockPos(382, 42, 726), BlockPos(383, 42, 726), BlockPos(389, 42, 726), BlockPos(390, 42, 726), BlockPos(382, 42, 738), BlockPos(383, 42, 738), BlockPos(389, 42, 738), BlockPos(390, 42, 738), BlockPos(382, 42, 738), BlockPos(383, 42, 738), BlockPos(389, 42, 738), BlockPos(390, 42, 738), BlockPos(380, 42, 728), BlockPos(380, 42, 729), BlockPos(380, 42, 735), BlockPos(380, 42, 736), BlockPos(392, 42, 728), BlockPos(392, 42, 729), BlockPos(392, 42, 735), BlockPos(392, 42, 736), BlockPos(383, 42, 725), BlockPos(381, 42, 726), BlockPos(380, 42, 727), BlockPos(379, 42, 729), BlockPos(379, 42, 735), BlockPos(380, 42, 737), BlockPos(381, 42, 738), BlockPos(383, 42, 739), BlockPos(389, 42, 739), BlockPos(391, 42, 738), BlockPos(392, 42, 737), BlockPos(393, 42, 735), BlockPos(393, 42, 729), BlockPos(392, 42, 727), BlockPos(391, 42, 726), BlockPos(389, 42, 725))
}