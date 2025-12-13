package me.ghluka.camel.module.modules.player

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse

class AutoPlace : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Auto Place"
        @Exclude
        const val CATEGORY = "Player"
        @Exclude
        const val DESCRIPTION = "Automatically places blocks when looking at valid positions."
    }

    @Exclude
    @Info(text = DESCRIPTION, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE)
    override var moduleEnabled = false

    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE)
    var moduleKeyBind = OneKeyBind()

    @Slider(
        name = "Delay (frames)", category = CATEGORY, subcategory = MODULE,
        min = 0f, max = 30f
    )
    var frameDelay = 0f

    @Switch(name = "Only if right clicking", category = CATEGORY, subcategory = MODULE)
    var holdRightClick = true

    @Exclude
    private val minPlaceIntervalMs = 25L

    @Exclude
    private var lastPlaceTime = 0L
    @Exclude
    private var frameCounter = 0

    @Exclude
    private var lastRaytrace: MovingObjectPosition? = null
    @Exclude
    private var lastBlockPos: BlockPos? = null

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onDrawBlockHighlight(event: DrawBlockHighlightEvent) {
        if (!moduleEnabled) return
        if (mc.currentScreen != null) return
        if (mc.thePlayer == null || mc.theWorld == null) return

        val heldItem = mc.thePlayer.heldItem ?: return
        if (heldItem.item !is ItemBlock) return

        val ray = mc.objectMouseOver ?: return
        if (ray.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return
        if (ray.sideHit == EnumFacing.UP || ray.sideHit == EnumFacing.DOWN) return

        if (lastRaytrace != null && frameCounter < frameDelay.toInt()) {
            frameCounter++
            return
        }

        lastRaytrace = ray
        val pos = ray.blockPos
        val lastPos = lastBlockPos

        if (lastPos == null ||
            pos.x != lastPos.x ||
            pos.y != lastPos.y ||
            pos.z != lastPos.z
        ) {
            val block = mc.theWorld.getBlockState(pos).block
            if (block == null || block == Blocks.air || block is BlockLiquid) return

            if (!holdRightClick || Mouse.isButtonDown(1)) {
                val now = System.currentTimeMillis()
                if (now - lastPlaceTime >= minPlaceIntervalMs) {
                    lastPlaceTime = now

                    val success = mc.playerController.onPlayerRightClick(
                        mc.thePlayer,
                        mc.theWorld,
                        heldItem,
                        pos,
                        ray.sideHit,
                        ray.hitVec
                    )

                    if (success) {
                        mc.thePlayer.swingItem()
                        mc.itemRenderer.resetEquippedProgress()

                        lastBlockPos = pos
                        frameCounter = 0
                    }
                }
            }
        }
    }
}
