package me.ghluka.camel.module.modules.misc

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.utils.ChatUtils
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*


class Hilarity : me.ghluka.camel.module.Module("Hilarity") {
    @Exclude
    @Info(text = "Plz admin no ban", subcategory = "Hilarity", category = "Misc", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Hilarity", category = "Misc", subcategory = "Hilarity", size = 1)
    override var moduleEnabled: Boolean = true
    @KeyBind(name = "", category = "Misc", subcategory = "Hilarity", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Chance %", category = "Misc", subcategory = "Hilarity", min = 1F, max = 100F, step = 1)
    var chanceHilarity: Float = 20F
    @Slider(name = "Interval (s)", category = "Misc", subcategory = "Hilarity", min = 0F, max = 120F, step = 1)
    var intervalHilarity: Float = 30F

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    private val messages = arrayOf(
            "Here to give you a heads up, you're getting wiped.",
            "You really thought that wasnt detectable?",
            "Hey! How you doin' ;)",
            "You know I can see chat messages, right?",
            "Just checked your collections, this ain't looking too good",
            "You know you're flagging right?",
            "Any last words?"
    )

    private var ticksSince = 0.0

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (event.phase === TickEvent.Phase.END) {
            ticksSince += 1.0

            if (ticksSince < intervalHilarity * 20)
                return

            val random: Int = Random().nextInt(100)
            if (random <= chanceHilarity)
                prank()

            ticksSince = 0.0
        }
    }

    fun prank() {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val prompt: IChatComponent = ChatComponentText(ChatUtils.prefix + ChatUtils.players.get(Random().nextInt(ChatUtils.players.size)) + ChatUtils.suffix + messages.get(Random().nextInt(messages.size)))
        mc.thePlayer.addChatMessage(prompt)
    }
}