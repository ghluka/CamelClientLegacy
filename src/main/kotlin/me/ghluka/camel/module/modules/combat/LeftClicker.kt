package me.ghluka.camel.module.modules.combat

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.pages.DefaultCombatPage
import me.ghluka.camel.utils.ReflectionUtils
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


class LeftClicker : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Left Clicker"
        @Exclude
        const val CATEGORY = "Combat"
    }

    @Exclude
    @Info(text = "Hold left click to autoclick!", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Min Clicks per Second", category = CATEGORY, subcategory = MODULE, min = 1F, max = 25F)
    var minCPS: Float = 9F
    @Slider(name = "Max Clicks per Second", category = CATEGORY, subcategory = MODULE, min = 1F, max = 25F)
    var maxCPS: Float = 13F
    @Slider(name = "Jitter", category = CATEGORY, subcategory = MODULE, min = 0F, max = 3F)
    var jitter: Float = 0F

    @Switch(name = "Break blocks", category = CATEGORY, subcategory = MODULE, size = 1)
    var breakBlocks: Boolean = true

    @Switch(name = "Shortbow filter override", category = CATEGORY, subcategory = MODULE, size = 1)
    var shortbowMode: Boolean = false

    @Page(category = CATEGORY, subcategory = MODULE, name = "$MODULE Filters", location = PageLocation.BOTTOM)
    var defaultCombatPage: DefaultCombatPage = DefaultCombatPage()

    @Exclude
    val r = Random
    @Exclude
    var timer = 0L

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (mc.currentScreen != null) return
        if (!Mouse.isButtonDown(0)) return

        if (defaultCombatPage.result() && !(shortbowMode && SkyblockUtils.loreContains(mc.thePlayer.heldItem, "Shortbow: Instantly shoots!"))) return

        // clicker
        val min = min(minCPS, maxCPS).toDouble()
        val max = max(minCPS, maxCPS).toDouble()

        val random = r.nextFloat()
        val biased = if (Random.nextBoolean()) random * random else 1 - (random * random)
        val debounce = 1000 / (min + (max - min) * biased) // deviation

        if (breakBlocks && mc.objectMouseOver != null) {
            try {
                val pos: BlockPos = mc.objectMouseOver.blockPos
                val state: Block = mc.theWorld.getBlockState(pos).block
                if (state != Blocks.air && state !is BlockLiquid) {
                    timer = System.currentTimeMillis() + debounce.toLong()
                    return
                }
            }
            catch (_ : NullPointerException) {} // nice
        }

        if (System.currentTimeMillis() > timer) {
            //println("waited ${System.currentTimeMillis()-timer}ms between clicks")
            try {
                val field = mc.javaClass.getDeclaredField("field_71429_W")
                field.setAccessible(true)
                field.set(mc, 0)
            } catch (_: Exception) {
                try {
                    val field = mc.javaClass.getDeclaredField("leftClickCounter")
                    field.setAccessible(true)
                    field.set(mc, 0)
                } catch (_: Exception) {}
            }
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)

            timer = System.currentTimeMillis() + debounce.toLong()
        }
        else if (System.currentTimeMillis() > timer + 11) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
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