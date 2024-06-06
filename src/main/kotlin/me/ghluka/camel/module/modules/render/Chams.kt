package me.ghluka.camel.module.modules.render

import cc.polyfrost.oneconfig.config.elements.SubConfig
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

class Chams : SubConfig("Chams", "camel/chams.json", null, false) {
    //@KeyBind(
    //    name = "Toggle Chams",
    //    size = 2
    //)
    //var chamsKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        MinecraftForge.EVENT_BUS.register(this)
        //registerKeyBind(chamsKeyBind) {
        //    enabled = !enabled
        //}
    }

    @SubscribeEvent
    fun onPreRenderPlayer(e: RenderPlayerEvent.Pre) {
        if (!enabled) return
        if (e.entity !== mc.thePlayer) {
            GL11.glEnable(32823)
            GL11.glPolygonOffset(1.0f, -1100000.0f)
        }
    }

    @SubscribeEvent
    fun onPostRenderPlayer(e: RenderPlayerEvent.Post) {
        if (!enabled) return
        if (e.entity !== mc.thePlayer) {
            GL11.glDisable(32823)
            GL11.glPolygonOffset(1.0f, 1100000.0f)
        }
    }
}