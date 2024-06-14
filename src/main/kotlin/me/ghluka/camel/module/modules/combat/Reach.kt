package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


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
    @Page(category = "Combat", subcategory = "Reach", name = "Reach filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    @Exclude
    var expand: Double = 0.0

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
        if (defaultCombatPage.result()) return

        if (!hitThroughWalls) {
            val p = mc.objectMouseOver.blockPos
            if (p != null && mc.theWorld.getBlockState(p).block !== Blocks.air) {
                return
            }
        }

        val eyes = Minecraft.getMinecraft().renderViewEntity.getPositionEyes(1.0f)
        val movingObject: MovingObjectPosition? = PlayerUtils.getMouseOver(maxRange.toDouble(), expand)
        if (movingObject != null && eyes.distanceTo(movingObject.hitVec) >= minRange) {
            mc.objectMouseOver = movingObject
            mc.pointedEntity = movingObject.entityHit
        }
    }
}