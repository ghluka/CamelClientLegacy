package me.ghluka.camel.module.modules.render

import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

class Chams : me.ghluka.camel.module.Module("Chams") {
    @Switch(
        name = "Enable chams",
        category = "Render",
        subcategory = "Chams",
        size = 1
    )
    override var moduleEnabled: Boolean = false
    @KeyBind(
        name = "",
        category = "Render",
        subcategory = "Chams",
        size = 1
    )
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
        save()
    }

    @SubscribeEvent
    fun onPreRenderPlayer(e: RenderPlayerEvent.Pre) {
        if (!moduleEnabled) return
        if (e.entity !== mc.thePlayer) {
            GL11.glEnable(32823)
            GL11.glPolygonOffset(1.0f, -1100000.0f)
        }
    }

    @SubscribeEvent
    fun onPostRenderPlayer(e: RenderPlayerEvent.Post) {
        if (!moduleEnabled) return
        if (e.entity !== mc.thePlayer) {
            GL11.glDisable(32823)
            GL11.glPolygonOffset(1.0f, 1100000.0f)
        }
    }
}