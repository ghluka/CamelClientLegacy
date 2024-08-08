package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


class AnvilESP : me.ghluka.camel.module.Module("AnvilESP") {
    @Exclude
    @Info(text = "Shows where anvils will fall", subcategory = "Anvil ESP", category = "Arcade", type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Anvil ESP", category = "Arcade", subcategory = "Anvil ESP", size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = "Arcade", subcategory = "Anvil ESP", size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }


}