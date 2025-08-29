package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.LocrawEvent
import cc.polyfrost.oneconfig.events.event.SendPacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.BlockUtils
import me.ghluka.camel.utils.PlayerUtils
import me.ghluka.camel.utils.RotationUtils
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.*
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.abs


class PowderChestAura : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Powder Chest Aura"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = "This is NOT a solver, and will only work if you have the Great Explorer perk. If you want a solver use GumTune or Pizza client.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false
    @Exclude
    @Info(text = "Warning: The Watchdog ban risk is low, but staff ban risk is especially high with no rotation type.", subcategory = MODULE, category = CATEGORY, type = InfoType.ERROR, size = 2)
    var banWarning: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Rotation Speed", category = CATEGORY, subcategory = MODULE, min = 50F, max = 500F, step = 1)
    var rotationSpeed: Float = 200F

    @Slider(name = "Rotate Back Speed", category = CATEGORY, subcategory = MODULE, min = 50F, max = 500F, step = 1)
    var rotateBackSpeed: Float = 50F

    @Dropdown(name = "Rotation Type", category = CATEGORY, subcategory = MODULE,
        options = ["None", "Smooth Look", "Server Look"])
    var rotType: Int = 2

    @Switch(name = "Rotate back", category = CATEGORY, subcategory = MODULE, size = 1)
    var rotateBack = true

    @Switch(name = "Click through walls (for smooth look, no rotation type does this no matter what)", category = CATEGORY, subcategory = MODULE, size = 2)
    var xray = false

    @Switch(name = "Drill swap", category = CATEGORY, subcategory = MODULE, size = 1)
    var drillSwap = false
    @Text(name = "Powder drill name", category = CATEGORY, subcategory = MODULE,
        placeholder = "", secure = false, multiline = false)
    var drillName: String = "gemstone drill"

    @Exclude
    @Info(text = "Disabling all whitelist options makes it work anywhere.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info2: Boolean = false

    @Switch(name = "Whitelist Dungeons", category = CATEGORY, subcategory = MODULE, size = 1)
    var dungeons = true
    @Switch(name = "Whitelist Crystal Hollows", category = CATEGORY, subcategory = MODULE, size = 1)
    var hollows = true

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var closestChest: BlockPos? = null
    @Exclude
    var original: RotationUtils.Rotation? = null
    @Exclude
    var rotatingBack = false
    @Exclude
    var swapping = false
    @Exclude
    var ogSlot = 0
    @Exclude
    private var timestamp: Long = 0
    @Exclude
    private var drillTimer: Long = 0
    @Exclude
    val solved = ArrayList<BlockPos>()

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (swapping && drillTimer < System.currentTimeMillis()) {
            //println("swapping back")
            PlayerUtils.swapToSlot(ogSlot)
            swapping = false
        }
        if (rotatingBack) {
            try {
                if (MainMod.serverLookUtils.perspectiveEnabled)
                    original = RotationUtils.Rotation(
                        MainMod.serverLookUtils.cameraPitch % 360,
                        MainMod.serverLookUtils.cameraYaw % 360 + 180f
                    )
                MainMod.rotationUtils.smoothLook(original!!, rotateBackSpeed.toLong())
                val yawDiff = abs(original!!.yaw % 360 - mc.thePlayer.rotationYaw % 360)
                val pitchDiff = abs(original!!.pitch % 360 - mc.thePlayer.rotationPitch % 360)
                if ((yawDiff < .2 || yawDiff > 359.8) && (pitchDiff < .2 || pitchDiff > 359.8)) {
                    MainMod.serverLookUtils.perspectiveEnabled = false
                    rotatingBack = false
                    original = null
                    // skip a tick why not
                }
            }
            catch (_ : NullPointerException) {
                MainMod.serverLookUtils.perspectiveEnabled = false
                rotatingBack = false
                original = null
            }
            return
        }
        if (!moduleEnabled || mc.thePlayer == null || mc.theWorld == null) {
            closestChest = null
            MainMod.serverLookUtils.perspectiveEnabled = false
            return
        }
        if (event.phase === TickEvent.Phase.END)
            return

        when (location) {
            "Crystal Hollows" -> {
                if (dungeons && !hollows) return
            }
            "Dungeons" -> {
                if (hollows && !dungeons) return
            }
            else -> {
                if (dungeons || hollows) return
            }
        }

        if (closestChest != null && mc.thePlayer.getDistanceSq(
                closestChest!!.x + 0.5,
                closestChest!!.y + 0.5,
                closestChest!!.z + 0.5
        ) > 16) {
            //println("out of range")
            closestChest = null
        }

        if (closestChest == null) {
            closestChest = BlockUtils.getClosestBlock(4, 4, 4, this::isPowderChest)
        }
        if (closestChest != null) {
            if (original == null)
                original = RotationUtils.Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw)
            if (rotType == 1 || rotType == 2) {
                val rot = MainMod.rotationUtils.getRotation(closestChest!!)!!

                MainMod.serverLookUtils.perspectiveEnabled = rotType == 2
                MainMod.rotationUtils.smoothLook(rot, rotationSpeed.toLong())
            }
            else if (rotType == 0) {
                clickChest()
            }
            if (rotType == 1 || rotType == 2) {
                val eyes: Vec3 = mc.thePlayer.getPositionEyes(1f)
                val look: Vec3 = mc.thePlayer.getLook(1f).normalize()
                val targetCenter = Vec3(closestChest).addVector(0.5, 0.5, 0.5)
                val toTarget = targetCenter.subtract(eyes)
                val distance = toTarget.lengthVector()

                if (distance > 5) return

                val end = eyes.addVector(look.xCoord * 4, look.yCoord * 4, look.zCoord * 4)
                if (!xray) {val hit: MovingObjectPosition? = mc.theWorld.rayTraceBlocks(eyes, end, false, true, false)
                    if (hit != null &&
                            hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                            closestChest == hit.blockPos) {
                        clickChest()
                        MainMod.rotationUtils.reset()
                    }
                } else {
                    val state = mc.theWorld.getBlockState(closestChest)
                    val block: Block = state.block

                    block.setBlockBoundsBasedOnState(mc.theWorld, closestChest)
                    val bb: AxisAlignedBB? = block.getSelectedBoundingBox(mc.theWorld, closestChest)

                    if (bb != null) {
                        val hit = bb.calculateIntercept(eyes, end)
                        if (hit != null) {
                            clickChest()
                            MainMod.rotationUtils.reset()
                        }
                    }
                }
            }
        }
        else
            MainMod.serverLookUtils.perspectiveEnabled = false
    }

    fun clickChest() {
        if (timestamp > System.currentTimeMillis()) return //debounce
        solved.add(closestChest!!)
        //// do not use this for opening chests it bans you:
        ///val packet = C08PacketPlayerBlockPlacement(
        ///    closestChest,
        ///    if (closestChest!!.y.toDouble() + 0.5 < mc.thePlayer.posY + 1.7) 1 else 0,
        ///    mc.thePlayer.currentEquippedItem,
        ///    0.0f,
        ///    0.0f,
        ///    0.0f
        ///)
        ///mc.thePlayer.closeScreen()
        ///mc.thePlayer.sendQueue.addToSendQueue(packet)
        ///mc.netHandler.networkManager.sendPacket(C0APacketAnimation())
        val blockCenter = Vec3(closestChest).addVector(0.5, 0.5, 0.5)

        var facing = EnumFacing.getFacingFromVector(
            ( blockCenter.xCoord - mc.thePlayer.positionVector.xCoord).toFloat(),
            (-blockCenter.yCoord + mc.thePlayer.positionVector.yCoord).toFloat(),
            ( blockCenter.zCoord - mc.thePlayer.positionVector.zCoord).toFloat()
        )

        if (facing != EnumFacing.DOWN)
            facing = EnumFacing.UP

        //swapToDrill()
        mc.playerController.onPlayerRightClick(
            mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem,
            closestChest, facing, Vec3(closestChest!!.x + 0.5, closestChest!!.y + 0.5, closestChest!!.z + 0.5)
        )

        solved.add(closestChest!!)

        if (!isPowderChest(closestChest!!)) {
            closestChest = null
        }

        timestamp = System.currentTimeMillis() + rotationSpeed.toLong()
        if (rotateBack && original != null) {
            rotatingBack = true
        }
    }

    @SubscribeEvent
    fun clear(event: WorldEvent.Unload?) {
        if (!moduleEnabled) return
        solved.clear()
        rotatingBack = false
        closestChest = null
    }

    fun isPowderChest(blockPos: BlockPos): Boolean {
        return mc.theWorld.getBlockState(blockPos).block === Blocks.chest && !solved.contains(blockPos)
    }

    @Exclude
    var location = ""
    @Subscribe
    fun onLocraw(event: LocrawEvent) {
        location = event.info.mapName
    }

    @Subscribe
    fun onPacketSent(event: SendPacketEvent) {
        if (event.packet is C08PacketPlayerBlockPlacement) {
            val place = event.packet as C08PacketPlayerBlockPlacement
            if (moduleEnabled && place.position == closestChest) {
                swapToDrill()
            }
        }
    }

    fun swapToDrill() {
        if (!drillSwap) return
        //println("swap to drill")
        if (!swapping) {
            ogSlot = mc.thePlayer.inventory.currentItem
            PlayerUtils.pickItem { stack ->
                stack?.displayName?.lowercase()?.contains(drillName) == true
            }
        }

        drillTimer = System.currentTimeMillis() + 75 + SkyblockUtils.getPing()
        swapping = true
    }
}