package me.ghluka.camel.module.modules.hypixel.skyblock.dojo

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Items
import net.minecraft.util.EntitySelectors
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class ForceAIO : Module(SUBMODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Dojo Helper"
        @Exclude
        const val SUBMODULE = "Force AIO"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = SUBMODULE, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $SUBMODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Switch(name = "Correct mob ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    var espEnabled: Boolean = true
    @Switch(name = "Block wrong clicks", category = CATEGORY, subcategory = MODULE, size = 1)
    var blockWrongClicks: Boolean = true
    @Switch(name = "Hide wrong mobs", category = CATEGORY, subcategory = MODULE, size = 1)
    var hideWrongMobs: Boolean = true

    @Color(name = "ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var espColor: OneColor = OneColor(100, 100, 255, 30F)

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled || !espEnabled) return
        if (mc.thePlayer != null && mc.theWorld != null) {
            for (entity in mc.theWorld.getEntities(EntityZombie::class.java, EntitySelectors.selectAnything)) {
                try {
                    if ((entity as EntityZombie).getCurrentArmor(3).item == Items.leather_helmet)
                        entity.isInvisible = hideWrongMobs
                    else if (entity.name == "Zombie")
                        RenderUtils.ree(entity, espColor.rgb)
                } catch (x: NullPointerException) {
                    if (entity.name == "Zombie")
                        RenderUtils.ree(entity, espColor.rgb)
                }
            }
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

        val isTargetMob = target is EntityZombie
        if (!isTargetMob) return

        val name = target.displayName?.unformattedText ?: return
        try {
            if ((target as EntityZombie).getCurrentArmor(3).item == Items.leather_helmet) {
                e.isCanceled = true
            }
        }
        catch (_ : NullPointerException) {}
    }
}