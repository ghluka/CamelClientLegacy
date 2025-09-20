package me.ghluka.camel.module.modules.player

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.SendPacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.ChatUtils
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.random.Random

class CustomRank : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Custom Rank"
        @Exclude
        const val CATEGORY = "Player"
    }

    @Exclude
    @Info(text = "Gives you a custom rank on the Hypixel network.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false
    @Exclude
    @Info(text = "This will disband the party you're already in, and you also need party leader to use this.", subcategory = MODULE, category = CATEGORY, type = InfoType.WARNING, size = 2)
    var info2: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 2)
    override var moduleEnabled: Boolean = false

    @Dropdown(
        name = "Bypass Type",
        options = ["Custom", "VIP", "VIP+", "MVP", "MVP+", "MVP++", "YOUTUBE", "ADMIN", "PIG+++"],
        subcategory = MODULE, category = CATEGORY
    )
    var rank: Int = 1
    @Text(
        name = "Custom Rank", placeholder = "",
        secure = false, multiline = false,
        category = CATEGORY, subcategory = MODULE,
        size = 1
    )
    var customRank = "§c[§fAMBASSADOR§c]"

    init {
        initialize()
    }

    @Exclude
    var ourRank = ""

    @SubscribeEvent
    fun chat(event: ClientChatReceivedEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        
        val unformatted = event.message.unformattedText.replace("§[0-9a-fk-or]".toRegex(), "")
        val msg = event.message.formattedText
        val currentRank = "${if (rank == 0) customRank else getRank(rank)} ${mc.thePlayer.name}"

        try {
            val sender = msg.split(": ")[0]

            if (mc.thePlayer.name !in sender)
                return
            event.isCanceled = true

            var sbLvl = sender.substringBeforeLast("[")
            var user = "$sbLvl$currentRank"

            if (unformatted.startsWith("Guild > ")) {
                val guildTag = sender.split(mc.thePlayer.name, limit=2)[1].trim()
                user = "§r§2Guild > $currentRank $guildTag"
            }
            else {
                ourRank = "[${sender.split("[").last()}"
            }

            mc.thePlayer.addChatMessage(ChatComponentText(
                "$user§f: ${msg.split(": ", limit=2)[1]}"
                
            ))
        }
        catch (_ : Exception) {
            if (ourRank != "" && ourRank in msg) {
                println(unformatted)
                println(ourRank)
                println(currentRank)
                println(msg.replace(ourRank, currentRank))
                event.isCanceled = true
                mc.thePlayer.addChatMessage(ChatComponentText(
                    msg.replace(ourRank, currentRank)
                ))
            }
        }
    }

    fun getRank(rank: Int): String {
        val rank = mapOf(
            1 to ChatUtils.ranks["vip"],
            2 to ChatUtils.ranks["vipp"],
            3 to ChatUtils.ranks["mvp"],
            4 to ChatUtils.ranks["mvpp"],
            5 to ChatUtils.ranks["mvppp"],
            6 to ChatUtils.ranks["yt"],
            7 to ChatUtils.ranks["staff"],
            8 to ChatUtils.ranks["techno"],
        )[rank]
        return "$rank]"
    }
}