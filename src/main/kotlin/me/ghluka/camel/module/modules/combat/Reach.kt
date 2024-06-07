package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.item.ItemSword
import net.minecraft.potion.Potion
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard


class Reach : me.ghluka.camel.module.Module("Reach") {
    @Exclude
    @Info(text = "Increases your range in hitting people", subcategory = "Reach", category = "Combat", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable reach", category = "Combat", subcategory = "Reach", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Combat", subcategory = "Reach", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Minimum range", category = "Combat", subcategory = "Reach", min = 3F, max = 6F)
    var minRange: Float = 3F
    @Slider(name = "Maximum range", category = "Combat", subcategory = "Reach", min = 3F, max = 6F)
    var maxRange: Float = 4F

    @Switch(name = "Hit through walls", category = "Combat", subcategory = "Reach", size = 2)
    var hitThroughWalls: Boolean = false

    @Switch(name = "Only with weapon", category = "Combat", subcategory = "Reach", size = 1)
    var onlyWithWeapon: Boolean = false
    @Switch(name = "Only while targeting", category = "Combat", subcategory = "Reach", size = 1)
    var onlyWhileTargeting: Boolean = false
    @Switch(name = "Only on ground", category = "Combat", subcategory = "Reach", size = 1)
    var onlyOnGround: Boolean = false
    @Switch(name = "Only while moving", category = "Combat", subcategory = "Reach", size = 1)
    var onlyWhileMoving: Boolean = false
    @Switch(name = "Only while sprinting", category = "Combat", subcategory = "Reach", size = 1)
    var onlyWhileSprinting: Boolean = false
    @Switch(name = "Only with speed", category = "Combat", subcategory = "Reach", size = 1)
    var onlyWithSpeed: Boolean = false
    @Switch(name = "Disable while holding S", category = "Combat", subcategory = "Reach", size = 1)
    var disableWhileS: Boolean = false

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRenderTick(e: TickEvent.RenderTickEvent) {
        if (!moduleEnabled) return
        reach()
    }
    @SubscribeEvent
    fun onMouse(e: MouseEvent) {
        if (!moduleEnabled) return
        reach()
    }

    fun reach() {
        if (mc.thePlayer == null) return

        if (onlyWithWeapon && (mc.thePlayer.currentEquippedItem == null || mc.thePlayer.currentEquippedItem.item !is ItemSword)) return
        if (onlyWhileTargeting && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) return
        if (onlyOnGround && !mc.thePlayer.onGround) return
        if (onlyWhileMoving && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F) return
        if (onlyWhileSprinting && !mc.thePlayer.isSprinting) return
        if (onlyWithSpeed && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) return
        if (disableWhileS && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode)) return

        if (!hitThroughWalls) {
            val p = mc.objectMouseOver.blockPos
            if (p != null && mc.theWorld.getBlockState(p).block !== Blocks.air) {
                return
            }
        }

        val eyes = Minecraft.getMinecraft().renderViewEntity.getPositionEyes(1.0f)
        val movingObject: MovingObjectPosition? = PlayerUtils.getMouseOver(maxRange.toDouble(), 0.0)
        if (movingObject != null && eyes.distanceTo(movingObject.hitVec) >= minRange) {
            mc.objectMouseOver = movingObject
            mc.pointedEntity = movingObject.entityHit
        }
    }
}