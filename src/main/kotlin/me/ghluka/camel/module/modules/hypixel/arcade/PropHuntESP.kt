package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.entity.passive.EntityCow
import net.minecraft.entity.passive.EntityPig
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EntitySelectors
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color
import kotlin.math.sqrt


class PropHuntESP : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Prop Hunt ESP"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    @Info(text = "Renders which props have moved, tracking those which are hiders. Some false positives may show. (/play arcade_hide_and_seek_prop_hunt).", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @cc.polyfrost.oneconfig.config.annotations.Color(name = "ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var espColor: OneColor = OneColor(Color.red)

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    val tracked = mutableMapOf<EntityArmorStand, Int>()
    @Exclude
    val startPosition = mutableMapOf<Int, BlockPos>()

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!moduleEnabled) return
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (event.phase === TickEvent.Phase.END) return
        for (entity in mc.theWorld.getEntities(EntityArmorStand::class.java, EntitySelectors.selectAnything)) {
            try {
                if (!tracked.containsKey(entity)) {
                    tracked[entity] = 0
                }
                if (startPosition[entity.entityId] != null) {
                    val dx = entity.position.x - (startPosition[entity.entityId]?.x ?: 0)
                    val dy = entity.position.y - (startPosition[entity.entityId]?.y ?: 0)
                    val dz = entity.position.z - (startPosition[entity.entityId]?.z ?: 0)
                    val distance = sqrt((dx * dx + dy * dy + dz * dz).toDouble())
                    if ((tracked[entity] ?: 0) > 5) {
                        startPosition[entity.entityId] = entity.position
                    }
                    else if (distance > 2) {
                        tracked[entity] = (tracked[entity] ?: 0) + 1
                    }
                }
                if (!startPosition.containsKey(entity.entityId))
                    startPosition[entity.entityId] = entity.position
            }
            catch (x: NullPointerException) { }
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.theWorld == null || mc.thePlayer == null) return
        for (entity in tracked.keys) {
            try {
                if ((tracked[entity]?: 0) > 2 && !entity.isDead) {
                    //println("${entity.position} ${tracked[entity]}")
                    RenderUtils.ree(entity, espColor.rgb)
                }
            }
            catch (x: NullPointerException) { }
        }
    }

    @SubscribeEvent
    fun clear(event: WorldEvent.Unload?) {
        if (!moduleEnabled) return
        tracked.clear()
        startPosition.clear()
    }
}