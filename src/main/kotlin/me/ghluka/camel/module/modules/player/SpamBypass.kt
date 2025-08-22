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
    var timer = 0L
    @Exclude
    var currentMessage = -1

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

        if (timer + SkyblockUtils.getPing() + 60 < System.currentTimeMillis() &&
                message.startsWith("-----------------------------------------"))
            event.isCanceled = true
        else if (!message.startsWith("You cannot say the same message twice!"))
            return

        event.isCanceled = true

        currentMessage += 1
        if (currentMessage == 10) currentMessage += 1 // skips k because you cant say k over 3 times on hypixel
        if (currentMessage == 26) currentMessage = 0
        val suffix = ('a' + currentMessage).toString().repeat(5)

        val msg = "$lastSentMessage $suffix"
        if (msg.length <= 256)
            mc.thePlayer.sendChatMessage(msg)
    }

    @Exclude
    var lastSentMessage = ""

    @Subscribe
    fun onPacketSent(event: SendPacketEvent) {
        if (event.packet is C01PacketChatMessage) {
            val chat = event.packet as C01PacketChatMessage
            timer = System.currentTimeMillis()
            lastSentMessage = chat.message
        }
    }
}