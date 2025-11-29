package me.ghluka.camel.module.modules.movement

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.events.PlayerMoveEvent
import me.ghluka.camel.module.Module
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard


class SafeWalk : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Safe Walk"
        @Exclude
        const val CATEGORY = "Movement"
    }

    @Exclude
    @Info(text = "Stops you from walking off blocks.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Exclude
    //@Switch(name = "Shift", category = CATEGORY, subcategory = MODULE, size = 1)
    var shift = true
    @Switch(name = "Shift during jumps", category = CATEGORY, subcategory = MODULE, size = 1)
    var shiftOnJump = false
    @Switch(name = "Blocks only", category = CATEGORY, subcategory = MODULE, size = 1)
    var blocksOnly = true
    @Switch(name = "Wool only", category = CATEGORY, subcategory = MODULE, size = 1)
    var onlyWool = false
    @Switch(name = "On shift hold", category = CATEGORY, subcategory = MODULE, size = 1)
    var onShift = false
    @Switch(name = "Only when looking down", category = CATEGORY, subcategory = MODULE, size = 1)
    var onDown = true
    @Slider(name = "Minimum pitch", category = CATEGORY, subcategory = MODULE, min = 0F, max = 90F, step = 0)
    var pitchMax: Float = 70F

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var safeWalking = false // something something entitymixin

    @SubscribeEvent
    fun onPlayerMove(event: PlayerMoveEvent.Pre) {
        if (mc.thePlayer == null || mc.theWorld == null || !moduleEnabled) {
            safeWalking = false
            return
        }

        if (onShift && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
            safeWalking = false
            return
        }
        if (onDown && mc.thePlayer.rotationPitch < pitchMax)
            return blockShift()
        if (blocksOnly && (mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item !is ItemBlock))
            return blockShift()
        if (onlyWool && (mc.thePlayer.heldItem.item !is ItemBlock ||
            (mc.thePlayer.heldItem.item as ItemBlock).block != Blocks.wool))
            return blockShift()

        if (!shiftOnJump && !mc.thePlayer.onGround)
            return blockShift()

        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY - 1.0
        val z = mc.thePlayer.posZ
        val pos = BlockPos(
            MathHelper.floor_double(x),
            MathHelper.floor_double(y),
            MathHelper.floor_double(z)
        )

        if (!shift) {
            safeWalking = true
        }
        else if (mc.theWorld.isAirBlock(pos)) {
            safeWalking = false
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, true)
        }
        else {
            blockShift()
        }
    }

    fun blockShift() {
        safeWalking = false
        if (onShift && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
        }
        else if (shift && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
        }
    }
}