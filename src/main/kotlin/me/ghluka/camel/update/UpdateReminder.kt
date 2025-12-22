package me.ghluka.camel.update

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import net.minecraft.event.ClickEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent
import scala.swing.TextComponent
import java.net.URL
import java.util.Properties


class UpdateReminder {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    var timer = 0L

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (System.currentTimeMillis() < timer) return

        if (timer != 0L) {
            try {
                val content =
                    URL("https://raw.githubusercontent.com/ghluka/CamelClient/refs/heads/v0/gradle.properties").readText(
                        Charsets.UTF_8
                    )
                val props = Properties().apply {
                    load(content.byteInputStream())
                }
                val currVersion = MainMod.VERSION.split(".").first().toInt()
                val latestVersion = props.getProperty("mod_version").split(".").first().toInt()
                if (latestVersion > currVersion) {
                    val prefix = ChatComponentText("${EnumChatFormatting.RED}[${EnumChatFormatting.GOLD}ℂ${EnumChatFormatting.RED}] Camel${EnumChatFormatting.WHITE}: ")

                    val prefix1 = prefix.createCopy()
                    val message = ChatComponentText("You're using an outdated version of this mod!")
                    prefix1.appendSibling(message)
                    mc.thePlayer.addChatMessage(prefix1)

                    val prefix2 = prefix.createCopy()
                    val message2 = ChatComponentText("An update is available from ${EnumChatFormatting.GRAY}b$currVersion${EnumChatFormatting.WHITE} to ${EnumChatFormatting.GRAY}b$latestVersion${EnumChatFormatting.WHITE}!")
                    prefix2.appendSibling(message2)
                    mc.thePlayer.addChatMessage(prefix2)

                    val prefix3 = prefix.createCopy()
                    val clickable = ChatComponentText("${EnumChatFormatting.RED}[${EnumChatFormatting.GOLD}CLICK HERE${EnumChatFormatting.RED}]")
                    clickable.chatStyle = ChatStyle().setChatClickEvent(
                        ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/ghluka/CamelClient/releases/latest")
                    )
                    prefix3.appendSibling(clickable)
                    mc.thePlayer.addChatMessage(prefix3)
                }
            }
            catch (_ : Exception) {
                return
            } // maybe we're offline or something

            MinecraftForge.EVENT_BUS.unregister(this)
            return
        }

        timer = System.currentTimeMillis() + 2500
    }
}
