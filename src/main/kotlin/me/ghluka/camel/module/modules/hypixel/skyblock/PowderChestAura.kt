package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.events.PlayerMoveEvent
import me.ghluka.camel.utils.BlockUtils
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class PowderChestAura : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Powder Chest Aura"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = "This is NOT a solver, and will only work if you have the Great Explorer perk. If you want a solver use GumTune Client or Pizza client", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Exclude
    var rotationSpeed: Float = 200F

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var closestChest: BlockPos? = null
    @Exclude
    private var rotatingBack = false
    @Exclude
    private var timestamp: Long = 0
    @Exclude
    val solved = ArrayList<BlockPos>()

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (event.phase === TickEvent.Phase.END) return

        if (closestChest == null) {
            closestChest = BlockUtils.getClosestBlock(4, 4, 4, this::isPowderChest)
            timestamp = System.currentTimeMillis()
            //rotatingBack = false
        }
        if (closestChest != null) {
            //if (MainMod.rotationUtils.done) {
            //    MainMod.rotationUtils.serverSmoothLook(
            //        MainMod.rotationUtils.getRotation(closestChest!!)!!,
            //        rotationSpeed.toLong()
            //    )
            //}
            //if (MainMod.rotationUtils.endTime + 500 < System.currentTimeMillis()) {
            ///val packet = C08PacketPlayerBlockPlacement(
            ///    closestChest,
            ///    if (closestChest!!.y.toDouble() + 0.5 < mc.thePlayer.posY + 1.7) 1 else 0,
            ///    mc.thePlayer.currentEquippedItem,
            ///    0.0f,
            ///    0.0f,
            ///    0.0f
            ///)
            mc.playerController.onPlayerRightClick(
                mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem,
                closestChest, EnumFacing.UP, Vec3(closestChest!!.x + 0.5, closestChest!!.y + 0.5, closestChest!!.z + 0.5)
            )
            ///mc.thePlayer.closeScreen()
            ///mc.thePlayer.sendQueue.addToSendQueue(packet)
            ///mc.netHandler.networkManager.sendPacket(C0APacketAnimation())
            solved.add(closestChest!!)
            MainMod.rotationUtils.reset()
            //}
            if (!isPowderChest(closestChest!!)) {
                closestChest = null
                rotatingBack = true
                timestamp = System.currentTimeMillis();
            }
        }
    }

    @SubscribeEvent
    fun clear(event: WorldEvent.Unload?) {
        if (!moduleEnabled) return
        solved.clear()
        closestChest = null
    }

    private fun isPowderChest(blockPos: BlockPos): Boolean {
        return mc.theWorld.getBlockState(blockPos).block === Blocks.chest && !solved.contains(blockPos)
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    fun onUpdatePre(pre: PlayerMoveEvent.Pre?) {
        if (!moduleEnabled) {
            closestChest = null
            return
        }
        if (closestChest == null) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        //if (!rotatingBack) {
        //    MainMod.rotationUtils.updateServerLook()
        //}
    }
}