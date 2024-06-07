package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


class JigsawRushTriggerbot : me.ghluka.camel.module.Module("JigsawRushTriggerbot") {
    @Exclude
    @Info(text = "Automatically places block when facing puzzle in Jigsaw Rush", subcategory = "Jigsaw Rush Triggerbot", category = "Arcade", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Jigsaw Rush Triggerbot", category = "Arcade", subcategory = "Jigsaw Rush Triggerbot", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Arcade", subcategory = "Jigsaw Rush Triggerbot", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null) return
        if (mc.theWorld == null) return
        if (mc.objectMouseOver == null) return


        try {
            val canvasPos: BlockPos = mc.objectMouseOver.blockPos
            if (mc.theWorld.getBlockState(canvasPos).block === Blocks.snow) {
                val pos: BlockPos = mc.objectMouseOver.blockPos.offset(mc.objectMouseOver.sideHit)
                if (mc.theWorld.getBlockState(pos.down()).block !== Blocks.air) {
                    if ((if (mc.thePlayer.horizontalFacing === EnumFacing.NORTH) mc.theWorld.getBlockState(pos.west()).block else mc.theWorld.getBlockState(pos.east()).block) !== Blocks.air)
                        this.clickItemFromBlockPos(BlockPos(226, 7, 1819))
                    else if ((if (mc.thePlayer.horizontalFacing === EnumFacing.NORTH) mc.theWorld.getBlockState(pos.east()).block else mc.theWorld.getBlockState(pos.west()).block) !== Blocks.air)
                        this.clickItemFromBlockPos(BlockPos(226, 7, 1813))
                    else
                        this.clickItemFromBlockPos(BlockPos(226, 7, 1816))
                }
                else if (mc.theWorld.getBlockState(pos.up()).block !== Blocks.air) {
                    if ((if (mc.thePlayer.horizontalFacing === EnumFacing.NORTH) mc.theWorld.getBlockState(pos.west()).block else mc.theWorld.getBlockState(pos.east()).block) !== Blocks.air)
                        this.clickItemFromBlockPos(BlockPos(226, 13, 1819))
                    else if ((if (mc.thePlayer.horizontalFacing === EnumFacing.NORTH) mc.theWorld.getBlockState(pos.east()).block else mc.theWorld.getBlockState(pos.west()).block) !== Blocks.air)
                        this.clickItemFromBlockPos(BlockPos(226, 13, 1813))
                    else
                        this.clickItemFromBlockPos(BlockPos(226, 13, 1816))
                }
                else if ((if (mc.thePlayer.horizontalFacing === EnumFacing.NORTH) mc.theWorld.getBlockState(pos.west()).block else mc.theWorld.getBlockState(pos.east()).block) !== Blocks.air)
                    this.clickItemFromBlockPos(BlockPos(226, 10, 1819))
                else if ((if (mc.thePlayer.horizontalFacing === EnumFacing.NORTH) mc.theWorld.getBlockState(pos.east()).block else mc.theWorld.getBlockState(pos.west()).block) !== Blocks.air)
                    this.clickItemFromBlockPos(BlockPos(226, 10, 1813))
                else
                    this.clickItemFromBlockPos(BlockPos(226, 10, 1816))
            }
        } catch (nullPointerException: java.lang.NullPointerException) { }
    }

    private fun clickItemFromBlockPos(pos: BlockPos) {
        try {
            val target: IBlockState = mc.theWorld.getBlockState(pos)
            for (i in 0..8) {
                val item: ItemStack = mc.thePlayer.inventory.getStackInSlot(i)
                if (Item.getItemFromBlock(target.block as Block) !== item.item) continue
                if (mc.thePlayer.inventory.currentItem != i) {
                    mc.thePlayer.inventory.currentItem = i
                    PlayerUtils.rightClick()
                }
                break
            }
        } catch (nullPointerException: NullPointerException) { }
    }
}