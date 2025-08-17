package me.ghluka.camel.module.modules.render

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.PageLocation
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSkull
import net.minecraft.util.BlockPos
import net.minecraft.util.EntitySelectors
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.util.*


class PlayerESP : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Player ESP"
        @Exclude
        const val CATEGORY = "Render"
    }

    @Exclude
    @Info(text = "Renders an ESP over all players.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Player ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @cc.polyfrost.oneconfig.config.annotations.Color(name = "ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var espColor: OneColor = OneColor(100, 100, 255, 30F)

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer != null && mc.theWorld != null) {
            for (entity in mc.theWorld.getEntities(EntityPlayer::class.java, EntitySelectors.selectAnything)) {
                if (entity != mc.thePlayer)
                    RenderUtils.ree(entity, espColor.rgb)
            }
        }
    }
}