package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.random.Random


class GardenAntivoid : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Garden Anti-Void"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = "Warps you back to your garden spawn the second you are over the void.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
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
    private val queue: MutableList<() -> Unit> = mutableListOf()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.PlayerTickEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (!SkyblockUtils.isInGarden()) return

        if (timer > System.currentTimeMillis())
            return

        if (queue.isNotEmpty()) {
            val action = queue.removeAt(0)
            action.invoke()
            timer = System.currentTimeMillis()
            return
        }

        val pos = BlockPos(mc.thePlayer.posX, 66.0, mc.thePlayer.posZ)
        val block = mc.theWorld.getBlockState(pos).block


        if (block != Blocks.bedrock) {
            timer = System.currentTimeMillis() + r.nextInt(100, 300)
            queue.add {
                if (block != Blocks.bedrock) mc.thePlayer.sendChatMessage("/warp garden")
            }
        }
    }
}