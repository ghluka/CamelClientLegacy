package me.ghluka.camel.module.modules.player

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.events.event.SendPacketEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.random.Random


class SpamBypass : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Spam Bypass"
        @Exclude
        const val CATEGORY = "Player"
    }

    @Exclude
    @Info(text = "Bypasses Hypixel's anti-spam.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Exclude
    val r = Random
    @Exclude
    var timer = 0L

    @Exclude
    var ignore = false

    @Exclude
    var currentMessage = -1
    @Dropdown(name = "Bypass Type", options = ["Alpha", "Random"], subcategory = MODULE, category = CATEGORY)
    var bypass: Int = 0

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun chat(event: ClientChatReceivedEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        val message: String = event.message.unformattedText.replace("§[0-9a-fk-or]".toRegex(), "")

        if (timer + SkyblockUtils.getPing()*2 + 60 > System.currentTimeMillis() &&
                message.startsWith("-----------------------------------------"))
            event.isCanceled = true
        if (!message.startsWith("You cannot say the same message twice!"))
            return

        event.isCanceled = true

        var suffix = ""
        if (bypass == 0) {
            currentMessage += 1
            if (currentMessage == 10) currentMessage += 1 // skips k because you cant say k over 3 times on hypixel
            if (currentMessage == 26) currentMessage = 0
            suffix = ('a' + currentMessage).toString().repeat(5)
        }
        else if (bypass == 1) {
            val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            suffix =  (1..5)
                .map { chars[r.nextInt(chars.length)] }
                .joinToString("")
        }

        val msg = "$lastSentMessage $suffix"
        if (msg.length <= 256) {
            ignore = true
            mc.thePlayer.sendChatMessage(msg)
        }
    }

    @Exclude
    var lastSentMessage = ""

    @Subscribe
    fun onPacketSent(event: SendPacketEvent) {
        if (event.packet is C01PacketChatMessage) {
            val chat = event.packet as C01PacketChatMessage
            timer = System.currentTimeMillis()
            if (!ignore && (chat.message.startsWith("/") &&
                        (chat.message.startsWith("/ac ", ignoreCase = true) ||
                                chat.message.startsWith("/gc ", ignoreCase = true) ||
                                chat.message.startsWith("/oc ", ignoreCase = true))) ||
                !chat.message.startsWith("/"))
                lastSentMessage = chat.message
            ignore = false
        }
    }
}