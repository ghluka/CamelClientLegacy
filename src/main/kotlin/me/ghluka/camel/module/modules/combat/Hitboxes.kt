package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.client.Minecraft
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class Hitboxes : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Hitboxes"
        @Exclude
        const val CATEGORY = "Combat"
    }
    
    @Exclude
    @Info(text = "Increases the size of entity hitboxes", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable hitboxes", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = "Reach", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Expansion", category = CATEGORY, subcategory = MODULE, min = 0.1F, max = 2F)
    var expand: Float = 1F

    @Page(category = CATEGORY, subcategory = MODULE, name = "Hitbox filters", location = PageLocation.BOTTOM)
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
        if (defaultCombatPage.result()) return

        val eyes = Minecraft.getMinecraft().renderViewEntity.getPositionEyes(1.0f)
        val movingObject: MovingObjectPosition? = PlayerUtils.getReachMouseOver(3.0, expand.toDouble())
        if (movingObject != null) {
            mc.objectMouseOver = movingObject
            mc.pointedEntity = movingObject.entityHit
        }
    }
}