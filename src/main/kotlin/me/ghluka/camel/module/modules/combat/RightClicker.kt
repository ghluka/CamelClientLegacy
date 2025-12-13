package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.pages.DefaultBattlePage
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import me.ghluka.camel.module.modules.movement.SafeWalk
import me.ghluka.camel.utils.ReflectionUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemBow
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


class RightClicker : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Right Clicker"
        @Exclude
        const val CATEGORY = "Combat"
    }

    @Exclude
    @Info(text = "Hold right click to autoclick!", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Min Clicks per Second", category = CATEGORY, subcategory = MODULE, min = 1F, max = 25F)
    var minCPS: Float = 12F
    @Slider(name = "Max Clicks per Second", category = CATEGORY, subcategory = MODULE, min = 1F, max = 25F)
    var maxCPS: Float = 18F
    fun mlString(): String {
        return "${String.format("%.1f", minCPS)}-${String.format("%.1f", maxCPS)}"
    }
    @Slider(name = "Jitter", category = CATEGORY, subcategory = MODULE, min = 0F, max = 3F)
    var jitter: Float = 0F

    @Switch(name = "Blocks only", category = CATEGORY, subcategory = MODULE, size = 1)
    var onlyBlocks: Boolean = false
    @Switch(name = "Wool only", category = CATEGORY, subcategory = MODULE, size = 1)
    var onlyWool = false
    @Switch(name = "Allow bow", category = CATEGORY, subcategory = MODULE, size = 1)
    var disableOnBow: Boolean = true
    @Switch(name = "Allow eat", category = CATEGORY, subcategory = MODULE, size = 1)
    var disableOnEat: Boolean = true

    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultBattlePage = DefaultBattlePage()

    @Exclude
    val r = Random
    @Exclude
    var timer = 0L

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (mc.currentScreen != null) return
        if (!Mouse.isButtonDown(1)) return
        if (defaultCombatPage.result()) return

        // clicker
        val min = min(minCPS, maxCPS).toDouble()
        val max = max(minCPS, maxCPS).toDouble()

        val random = r.nextFloat()
        val biased = if (Random.nextBoolean()) random * random else 1 - (random * random)
        val debounce = 1000 / (min + (max - min) * biased) // deviation

        if (mc.thePlayer.heldItem != null) {
            if (onlyBlocks && mc.thePlayer.heldItem.item !is ItemBlock) {
                timer = System.currentTimeMillis() + debounce.toLong()
                return
            }
            if (onlyWool && (mc.thePlayer.heldItem.item !is ItemBlock ||
                        (mc.thePlayer.heldItem.item as ItemBlock).block != Blocks.wool)) {
                timer = System.currentTimeMillis() + debounce.toLong()
                return
            }

            if (disableOnBow && mc.thePlayer.heldItem.item is ItemBow) {
                timer = System.currentTimeMillis() + debounce.toLong()
                return
            }
        }

        if (disableOnEat && mc.thePlayer.isEating) {
            timer = System.currentTimeMillis() + debounce.toLong()
            return
        }

        if (System.currentTimeMillis() > timer) {
            //println("waited ${System.currentTimeMillis()-timer}ms between clicks")
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)

            timer = System.currentTimeMillis() + debounce.toLong()
        }
        else if (System.currentTimeMillis() > timer + 11) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)
        }

        // jitter
        if (jitter > 0) {
            mc.thePlayer.rotationYaw =
                mc.thePlayer.rotationYaw + r.nextFloat() * jitter * 0.45F * (if (r.nextBoolean()) 1 else -1)
            mc.thePlayer.rotationPitch =
                mc.thePlayer.rotationPitch + r.nextFloat() * jitter * 0.2F * (if (r.nextBoolean()) 1 else -1)
        }
    }

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }
}