package me.ghluka.camel.module.modules.render

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemEgg
import net.minecraft.item.ItemEnderPearl
import net.minecraft.item.ItemPotion
import net.minecraft.item.ItemSnowball
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class Projectiles : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Projectiles"
        @Exclude
        const val CATEGORY = "Render"
    }

    @Exclude
    @Info(text = "Renders a trajectory of where your projectile will land.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Switch(name = "Show Arrows", category = CATEGORY, subcategory = MODULE, size = 1)
    var arrows: Boolean = true
    @Switch(name = "Show Pearls", category = CATEGORY, subcategory = MODULE, size = 1)
    var pearls: Boolean = true
    @Switch(name = "Show Potions", category = CATEGORY, subcategory = MODULE, size = 1)
    var pots: Boolean = true
    @Switch(name = "Show Eggs", category = CATEGORY, subcategory = MODULE, size = 1)
    var eggs: Boolean = true
    @Switch(name = "Show Snowballs", category = CATEGORY, subcategory = MODULE, size = 1)
    var snowballs: Boolean = true

    @Color(name = "Trajectory color", category = CATEGORY, subcategory = MODULE, size = 1)
    var espColor: OneColor = OneColor(100, 100, 255, 30F)

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (mc.gameSettings.hideGUI) return

        val stack = mc.thePlayer.heldItem ?: return
        val item = stack.item

        if (!arrows && item is ItemBow) return
        if (!pearls && item is ItemEnderPearl) return
        if (!pots && item is ItemPotion) return
        if (!eggs && item is ItemEgg) return
        if (!snowballs && item is ItemSnowball) return

        if (item is ItemPotion && stack.metadata and 0x4000 == 0)
            return
        if (item is ItemBow && !(mc.thePlayer.isUsingItem && mc.thePlayer.itemInUse != null && mc.thePlayer.itemInUse.item is ItemBow))
            return

        RenderUtils.drawProjectilePrediction(mc.thePlayer, espColor.rgb)
    }
}