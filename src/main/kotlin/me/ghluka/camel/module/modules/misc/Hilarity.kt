package me.ghluka.camel.module.modules.misc

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.utils.ChatUtils
import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.lang.management.ManagementFactory
import java.util.*
import java.util.concurrent.TimeUnit


class Hilarity : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Hilarity"
        @Exclude
        const val CATEGORY = "Misc"
    }

    @Exclude
    @Info(text = "Plz admin no ban", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Exclude
    private val messages = arrayOf(
        "Here to give you a heads up, you're getting wiped.",
        "You really thought that wasn't detectable?",
        "Hey! How you doin' ;)",
        "You know I can see chat messages, right?",
        "Just checked your collections, this ain't looking too good",
        "You know you're flagging right?",
        "Any last words?",
        "Smile! Your stats are about to be reset.",
        "I see everything you're doing... and I'm not impressed.",
        "Fun while it lasted, huh?",
        "Guess who just got flagged? Spoiler: it's you.",
        "Let's take a moment to appreciate how obvious that was.",
        "Next time, maybe try skill instead of hacks.",
        "${EnumChatFormatting.LIGHT_PURPLE}( ﾟ◡ﾟ)/",
        "Funny, your click patterns are... interesting.",
        "Must be lucky lol.",
        "Interesting..."
    )

    @Switch(name = "Enable Hilarity", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = true
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Chance %", category = CATEGORY, subcategory = MODULE, min = 1F, max = 100F, step = 1)
    var chanceHilarity: Float = 10F
    @Slider(name = "Interval (s)", category = CATEGORY, subcategory = MODULE, min = 0F, max = 120F, step = 1)
    var intervalHilarity: Float = 120F

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

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

    @SubscribeEvent
    fun onGuiInit(event: InitGuiEvent.Post) {
        if (!moduleEnabled) return
        if (event.gui is GuiConnecting) {
            val gui = event.gui as GuiConnecting?

            //val manager = ReflectionHelper.getPrivateValue<NetworkManager?, GuiConnecting?>(
            //    GuiConnecting::class.java,
            //    gui,
            //    "field_146374_h",
            //    "networkManager"
            //)

            try {
                val data = mc.currentServerData
                //println(data.serverIP.lowercase())
                if (data != null && data.serverIP != null && data.serverIP.lowercase().contains("hypixel.net")) {
                    val random: Int = Random().nextInt(100)
                    if (random <= chanceHilarity) {
                        mc.displayGuiScreen(
                            GuiDisconnected(
                                mc.currentScreen,
                                "connect.failed",
                                ChatComponentText(
                                    "${EnumChatFormatting.RED}You are temporarily banned for " +
                                            "${EnumChatFormatting.WHITE}${getUptimeString(TimeUnit.DAYS.toMillis(365))}" +
                                            "${EnumChatFormatting.RED} from this server!\n\n" +

                                            "${EnumChatFormatting.GRAY}Reason: ${EnumChatFormatting.WHITE}Cheating through the use of unfair game advantages.\n" +
                                            "${EnumChatFormatting.GRAY}Find out more: ${EnumChatFormatting.AQUA}${EnumChatFormatting.UNDERLINE}https://www.hypixel.net/appeal\n\n" +

                                            "${EnumChatFormatting.GRAY}Ban ID: ${EnumChatFormatting.WHITE}#E57970DA\n" +
                                            "${EnumChatFormatting.GRAY}Sharing your Ban ID may affect the processing of your appeal!"
                                )
                            )
                        )
                    }
                }
            }
            catch (_ : NullPointerException) {} // no currentServerData
        }
    }

    @Exclude
    private var ticksSince = 0.0

    fun prank() {
        if (mc.thePlayer == null || mc.theWorld == null) return
        var msg = ChatUtils.prefix + ChatUtils.players.get(Random().nextInt(ChatUtils.players.size)) + ChatUtils.suffix + messages.get(Random().nextInt(messages.size))
        if ("++" !in msg && ("MVP" in msg || "VIP" in msg))
            msg = msg.replace("${EnumChatFormatting.LIGHT_PURPLE}( ﾟ◡ﾟ)/", "o/")
        mc.thePlayer.addChatMessage(ChatComponentText(msg))
    }

    fun getUptimeString(x: Long = 0): String {
        var duration = MainMod.startup - System.currentTimeMillis() + x

        val days: Long = TimeUnit.MILLISECONDS.toDays(duration)
        duration -= TimeUnit.DAYS.toMillis(days)

        val hours: Long = TimeUnit.MILLISECONDS.toHours(duration)
        duration -= TimeUnit.HOURS.toMillis(hours)

        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(duration)
        duration -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(duration)

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds)
    }
}