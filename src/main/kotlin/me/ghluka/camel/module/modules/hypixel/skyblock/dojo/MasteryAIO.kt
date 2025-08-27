package me.ghluka.camel.module.modules.hypixel.skyblock.dojo

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent
import cc.polyfrost.oneconfig.events.event.SendPacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockColored
import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class MasteryAIO : Module(SUBMODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Dojo Helper"
        @Exclude
        const val SUBMODULE = "Mastery Aimbot"// AIO"
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

    @Switch(name = "Aim at Green", category = CATEGORY, subcategory = MODULE, size = 1)
    var green: Boolean = false
    @Switch(name = "Aim at Yellow", category = CATEGORY, subcategory = MODULE, size = 1)
    var yellow: Boolean = true
    @Switch(name = "Aim at Red", category = CATEGORY, subcategory = MODULE, size = 1)
    var red: Boolean = true

    @Switch(name = "Lock-on", category = CATEGORY, subcategory = MODULE, size = 1)
    var lockon: Boolean = true
    //@Slider(name = "Bow charge", category = CATEGORY, subcategory = MODULE, min = 0.1F, max = 1F)
    //var charge: Float = 0.6F

    @Exclude
    var lockedPos: BlockPos? = null

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
        //if (mc.thePlayer.currentEquippedItem != null && mc.thePlayer.currentEquippedItem.item == Items.bow) {
        //    if (!mc.thePlayer.isUsingItem) {
        //        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.currentEquippedItem)
        //        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)
        //    }
        //}
        if (!MainMod.rotationUtils.done) return
        if (lockedPos != null && mc.theWorld.getBlockState(lockedPos).block != Blocks.wool)
            lockedPos = null
        if (lockon && lockedPos != null) {
            val rot = MainMod.rotationUtils.getRotation(lockedPos!!)
            if (rot != null) {
                rot.pitch -= 3f
                MainMod.rotationUtils.smoothLook(rot, 100)
            }
            return
        }
        lockedPos = null
        //// THIS BANS :(
        //if (mc.thePlayer.isUsingItem && mc.thePlayer.itemInUse.item === Items.bow) {
        //    val bow = mc.thePlayer.itemInUse.item as ItemBow
        //    val i = bow.getMaxItemUseDuration(mc.thePlayer.itemInUse) - mc.thePlayer.itemInUseCount
        //    var f2 = i.toFloat() / 20.0f
        //    f2 = (f2 * f2 + f2 * 2.0f) / 3.0f
        //
        //    if (f2 >= charge) {
        //        mc.netHandler.addToSendQueue(
        //            C08PacketPlayerBlockPlacement(mc.thePlayer.currentEquippedItem)
        //        )
        //        mc.netHandler.addToSendQueue(
        //            C07PacketPlayerDigging(
        //                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
        //                BlockPos.ORIGIN,
        //                EnumFacing.DOWN
        //            )
        //        )
        //        mc.thePlayer.stopUsingItem()
        //        mc.playerController.updateController()
        //    }
        //}

        for (block in pillars) {
            try {
                if (mc.theWorld.getBlockState(block).block == Blocks.wool &&
                    (green && mc.theWorld.getBlockState(block).getValue(BlockColored.COLOR) == EnumDyeColor.LIME) ||
                    (yellow && mc.theWorld.getBlockState(block).getValue(BlockColored.COLOR) == EnumDyeColor.YELLOW) ||
                    (red && mc.theWorld.getBlockState(block).getValue(BlockColored.COLOR) == EnumDyeColor.RED)
                ) {
                    lockedPos = block
                    val rot = MainMod.rotationUtils.getRotation(block)
                    if (rot != null) {
                        rot.pitch -= 3f
                        MainMod.rotationUtils.smoothLook(rot, 100)
                    }
                    break
                }
            }
            catch (_ : IllegalArgumentException) {} // nonsense
        }
    }

    @Exclude
    var pillars = mutableListOf<BlockPos>()
    @Subscribe
    fun onPacketReceived(event: ReceivePacketEvent) {
        if (event.packet is S08PacketPlayerPosLook && SkyblockUtils.hasLine("Dojo")) {
            val tp = event.packet as S08PacketPlayerPosLook
            // test in singleplayer with /tp -206.5 100 -597.5
            if (moduleEnabled && tp.x == -206.5 && tp.y == 100.0 && tp.z == -597.5) {
                pillars.clear()
                for (block in BlockPos.getAllInBox(
                    BlockPos(-192, 106, -614),
                    BlockPos(-223, 98, -582)
                )) {
                    try {
                        if (mc.theWorld.getBlockState(block).block == Blocks.log) {
                            var pos = block
                            while (mc.theWorld.getBlockState(pos.up()).block == Blocks.log) {
                                pos = pos.up()
                            }
                            pillars.add(pos.up())
                        }
                    }
                    catch (_ : IllegalArgumentException) {} // nonsense
                }
            }
        }
    }
}