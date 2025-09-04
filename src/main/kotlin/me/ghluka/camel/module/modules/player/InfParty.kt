package me.ghluka.camel.module.modules.player

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.SendPacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.random.Random

class InfParty : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Infinite Party"
        @Exclude
        const val CATEGORY = "Player"
    }

    @Exclude
    @Info(text = "Disbands and invites someone even if they haven't already accepted your last invite, allowing you to flood their chat with party invites.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false
    @Exclude
    @Info(text = "This will disband the party you're already in, and you also need party leader to use this.", subcategory = MODULE, category = CATEGORY, type = InfoType.WARNING, size = 2)
    var info2: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Delay (ms)", category = CATEGORY, subcategory = MODULE, min = 200F, max = 550F)
    var speed: Float = 250F

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var timer = 0L
    @Exclude
    var stage = 0

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (mc.thePlayer == null || mc.theWorld == null || !moduleEnabled) {
            lastSentMessage = ""
            return
        }
        if (timer == 0L) return

        if (stage == 1 && timer < System.currentTimeMillis()) {
            timer = System.currentTimeMillis() + 55
            mc.thePlayer.sendChatMessage("/p disband")
        }
        else if (stage == 2 && timer < System.currentTimeMillis()) {
            timer = System.currentTimeMillis() + 55
            mc.thePlayer.sendChatMessage(lastSentMessage)
        }
    }

    @SubscribeEvent
    fun chat(event: ClientChatReceivedEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        val message: String = event.message.unformattedText.replace("§[0-9a-fk-or]".toRegex(), "")

        if (message.contains(":")) return

        if (stage == 0 && message.split(" ", limit=2)[1] == "has already been invited to the party.") {
            event.isCanceled = true
            timer = System.currentTimeMillis() + speed.toLong()
            stage = 1
        }
        else if (stage == 1 && message.endsWith("has disbanded the party!")) {
            event.isCanceled = true
            timer = System.currentTimeMillis() + speed.toLong()
            stage = 2
        }
        else if (stage == 2 && message.endsWith("They have 60 seconds to accept.")) {
            stage = 0
        }
        else if ((stage == 1 || stage == 2) &&
            (message == "Woah slow down, you're doing that too fast!" ||
             message == "-----------------------------------------------------") ||
             message == "Command Failed: This command is on cooldown! Try again in about a second!")
            event.isCanceled = true
    }

    @Exclude
    var lastSentMessage = ""

    @Subscribe
    fun onPacketSent(event: SendPacketEvent) {
        if (event.packet is C01PacketChatMessage) {
            val chat = event.packet as C01PacketChatMessage
            if (timer == 0L && (chat.message.startsWith("/p ") ||
                chat.message.startsWith("/party ")))
                lastSentMessage = chat.message
        }
    }
}