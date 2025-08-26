package me.ghluka.camel.module.modules.hypixel.skyblock.dojo

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.util.BlockPos
import net.minecraft.util.EntitySelectors
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class StaminaESP : Module(SUBMODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Dojo Helper"
        @Exclude
        const val SUBMODULE = "Stamina ESP"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = SUBMODULE, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $SUBMODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Color(name = "East/West Color", category = CATEGORY, subcategory = MODULE, size = 1)
    var ewColor: OneColor = OneColor(java.awt.Color.magenta)
    @Color(name = "North/South Color", category = CATEGORY, subcategory = MODULE, size = 1)
    var nsColor: OneColor = OneColor(java.awt.Color.pink)

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

        for (block in BlockPos.getAllInBox(
            BlockPos(-192, 105, -598),
            BlockPos(-222, 105, -598),
        )) {
            esp(block)
        }
        for (block in BlockPos.getAllInBox(
            BlockPos(-207, 105, -583),
            BlockPos(-207, 105, -613),
        )) {
            esp(block)
        }
    }

    fun esp(block: BlockPos) {
        if (mc.theWorld.getBlockState(block).block == Blocks.stone &&
            mc.theWorld.getBlockState(block.west()).block == Blocks.stone &&
            mc.theWorld.getBlockState(block.east()).block == Blocks.stone) {
            // north or south
            for (b in BlockPos.getAllInBox(
                BlockPos(-192, 105, block.z),
                BlockPos(-222, 100, block.z),
            )) {
                if (mc.theWorld.getBlockState(b).block == Blocks.air)
                    RenderUtils.re(b, nsColor.rgb)
            }
        }
        else if (mc.theWorld.getBlockState(block).block == Blocks.stone &&
            mc.theWorld.getBlockState(block.north()).block == Blocks.stone &&
            mc.theWorld.getBlockState(block.south()).block == Blocks.stone) {
            // east or west
            for (b in BlockPos.getAllInBox(
                BlockPos(block.x, 105, -583),
                BlockPos(block.x, 100, -613),
            )) {
                if (mc.theWorld.getBlockState(b).block == Blocks.air)
                    RenderUtils.re(b, ewColor.rgb)
            }
        }
    }
}