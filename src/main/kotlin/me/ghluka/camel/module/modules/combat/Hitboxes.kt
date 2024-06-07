package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.config.pages.DefaultCombatPage
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


class Hitboxes : me.ghluka.camel.module.Module("Hitboxes") {
    @Exclude
    @Info(text = "Increases the size of entity hitboxes", subcategory = "Hitboxes", category = "Combat", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable hitboxes", category = "Combat", subcategory = "Hitboxes", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Combat", subcategory = "Reach", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Expansion", category = "Combat", subcategory = "Hitboxes", min = 0.1F, max = 2F)
    var expand: Float = 1F

    @Page(category = "Combat", subcategory = "Hitboxes", name = "Hitbox filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRenderTick(e: TickEvent.RenderTickEvent) {
        var reach: Reach? = MainMod.moduleManager.getModuleByName("Reach") as Reach?
        if (reach != null) {
            reach.expand = 0.0
        }
        if (!moduleEnabled) return
        if (reach != null && reach.moduleEnabled)
            reach.expand = expand.toDouble()
        else
            hitboxes()
    }
    @SubscribeEvent
    fun onMouse(e: MouseEvent) {
        var reach: Reach? = MainMod.moduleManager.getModuleByName("Reach") as Reach?
        if (reach != null) {
            reach.expand = 0.0
        }
        if (!moduleEnabled) return
        if (reach != null && reach.moduleEnabled)
            reach.expand = expand.toDouble()
        else
            hitboxes()
    }

    fun hitboxes() {
        if (mc.thePlayer == null) return

        if (defaultCombatPage.onlyWithWeapon && (mc.thePlayer.currentEquippedItem == null || mc.thePlayer.currentEquippedItem.item !is ItemSword)) return
        if (defaultCombatPage.onlyWhileTargeting && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) return
        if (defaultCombatPage.onlyOnGround && !mc.thePlayer.onGround) return
        if (defaultCombatPage.onlyWhileMoving && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F) return
        if (defaultCombatPage.onlyWhileSprinting && !mc.thePlayer.isSprinting) return
        if (defaultCombatPage.onlyWithSpeed && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) return
        if (defaultCombatPage.disableWhileS && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode)) return

        val eyes = Minecraft.getMinecraft().renderViewEntity.getPositionEyes(1.0f)
        val movingObject: MovingObjectPosition? = PlayerUtils.getMouseOver(3.0, expand.toDouble())
        if (movingObject != null) {
            mc.objectMouseOver = movingObject
            mc.pointedEntity = movingObject.entityHit
        }
    }
}