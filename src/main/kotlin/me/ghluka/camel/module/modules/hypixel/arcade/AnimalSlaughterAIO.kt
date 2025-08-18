package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.PageLocation
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.entity.passive.EntityCow
import net.minecraft.entity.passive.EntityPig
import net.minecraft.util.BlockPos
import net.minecraft.util.EntitySelectors
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color


class AnimalSlaughterAIO : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Animal Slaughter AIO"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    @Info(text = "Renders an ESP over the correct mobs to kill, optionally blocks wrong mob clicks for the game Party Games (/play party_games).", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Switch(name = "Correct mob ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    var espEnabled: Boolean = true
    @Switch(name = "Block wrong clicks", category = CATEGORY, subcategory = MODULE, size = 1)
    var blockWrongClicks: Boolean = true
    @Switch(name = "Hide wrong mobs", category = CATEGORY, subcategory = MODULE, size = 1)
    var hideWrongMobs: Boolean = true

    @cc.polyfrost.oneconfig.config.annotations.Color(name = "Cow ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var cowColor: OneColor = OneColor(45, 35, 20)
    @cc.polyfrost.oneconfig.config.annotations.Color(name = "Pig ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var pigColor: OneColor = OneColor(Color.pink)
    @cc.polyfrost.oneconfig.config.annotations.Color(name = "Chicken ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var chickenColor: OneColor = OneColor(Color.white)

    @Page(category = CATEGORY, subcategory = MODULE, name = "Animal ESP Filters", location = PageLocation.BOTTOM)
    var mobPage: MobPage = MobPage()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onMouse(e: MouseEvent) {
        if (!moduleEnabled || !blockWrongClicks) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (e.button != 0 || !e.buttonstate) return

        val mop = mc.objectMouseOver ?: return
        if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return
        val target = mop.entityHit ?: return

        val isTargetMob = target is EntityCow || target is EntityPig || target is EntityChicken
        if (!isTargetMob) return

        val name = target.displayName?.unformattedText ?: return
        if ('-' in name) {
            e.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled || !espEnabled) return
        if (mc.thePlayer != null && mc.theWorld != null) {
            renderMob(mobPage.cowESP, EntityCow::class.java, e, Color(cowColor.rgb))
            renderMob(mobPage.pigESP, EntityPig::class.java, e, Color(pigColor.rgb))
            renderMob(mobPage.chickenESP, EntityChicken::class.java, e, Color(chickenColor.rgb))
            for (entity in mc.theWorld.getEntities(EntityAnimal::class.java, EntitySelectors.selectAnything)) {
                try {
                    if ("-" in entity.name)
                        entity.isInvisible = hideWrongMobs
                } catch (x: NullPointerException) {}
            }
        }
    }

    fun renderMob(page: Boolean, entityMob: Class<out Entity>, e: RenderWorldLastEvent?, color: Color) {
        if (!page) return;
        for (entity in mc.theWorld.getEntities(entityMob, EntitySelectors.selectAnything)) {
            try {
                var render = false
                if ("+" in entity.name)
                    render = true

                if (render) {
                    RenderUtils.ree(entity, color.rgb)
                }
            } catch (x: NullPointerException) {}
        }
    }
}
class MobPage {
    @Switch(name = "Cow ESP", size = 1)
    var cowESP: Boolean = true
    
    @Switch(name = "Pig ESP", size = 1)
    var pigESP: Boolean = true
    
    @Switch(name = "Chicken ESP", size = 1)
    var chickenESP: Boolean = true
}