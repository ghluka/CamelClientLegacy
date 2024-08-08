package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EntitySelectors
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import me.ghluka.camel.utils.RenderUtils
import java.awt.Color

class AnvilESP : me.ghluka.camel.module.Module("AnvilESP") {
    @Exclude
    @Info(text = "Shows where anvils will fall", subcategory = "Anvil ESP", category = "Hypixel Arcade", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Anvil ESP", category = "Hypixel Arcade", subcategory = "Anvil ESP", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Hypixel Arcade", subcategory = "Anvil ESP", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

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
            for (entity in mc.theWorld.getEntities(EntityFallingBlock::class.java, EntitySelectors.selectAnything)) {
                if (mc.theWorld.getBlockState(entity.position.down(1)).block === Blocks.air) {
                    val endPos = getFallingBlockTarget(entity)
                    if (!entity.onGround && entity.position.y >= endPos.y && mc.theWorld.getBlockState(endPos).block !== Blocks.air) {
                        RenderUtils.re(endPos, Color.RED.rgb)
                    }
                }
            }
        }
    }

    private fun getFallingBlockTarget(entity: EntityFallingBlock): BlockPos {
        var currentPos = entity.position
        while (currentPos.y > 1) {
            val blockBelow: Block = mc.theWorld.getBlockState(currentPos.down()).block
            if (blockBelow !== Blocks.air) {
                return currentPos.down()
            }
            currentPos = currentPos.down()
        }
        return BlockPos(currentPos.x, 0, currentPos.z)
    }
}