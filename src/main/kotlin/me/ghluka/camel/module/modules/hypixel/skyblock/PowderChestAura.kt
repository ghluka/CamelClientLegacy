package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.events.PlayerMoveEvent
import me.ghluka.camel.utils.BlockUtils
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class PowderChestAura : me.ghluka.camel.module.Module("PowderChestAura") {
    @Exclude
    @Info(text = "This is NOT a solver, and will only work if you have the Great Explorer perk. If you want a solver use GumTune Client or Pizza client", subcategory = "Powder Chest Aura", category = "Hypixel Skyblock", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Powder Chest Aura", category = "Hypixel Skyblock", subcategory = "Powder Chest Aura", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Hypixel Skyblock", subcategory = "Powder Chest Aura", size = 1)
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
            rotatingBack = false
        }
        if (closestChest != null) {
            if (MainMod.rotationUtils.done) {
                MainMod.rotationUtils.serverSmoothLook(
                    MainMod.rotationUtils.getRotation(closestChest!!)!!,
                    rotationSpeed.toLong()
                )
            }
            if (MainMod.rotationUtils.endTime + 500 < System.currentTimeMillis()) {
                val vec3 = mc.thePlayer.lookVec
                val vec32 = vec3.addVector(vec3.xCoord * 4, vec3.yCoord * 4, vec3.zCoord * 4)
                val hitResult = mc.thePlayer.worldObj.rayTraceBlocks(
                    mc.thePlayer.getPositionEyes(1f),
                    vec32,
                    false,
                    true,
                    false
                )
                if (mc.playerController.onPlayerRightClick(
                        mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem,
                        closestChest, hitResult.sideHit, hitResult.hitVec
                    )) {
                    mc.thePlayer.swingItem()
                    solved.add(closestChest!!)
                    MainMod.rotationUtils.reset()
                }
            }
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
        if (!rotatingBack) {
            MainMod.rotationUtils.updateServerLook()
        }
    }
}