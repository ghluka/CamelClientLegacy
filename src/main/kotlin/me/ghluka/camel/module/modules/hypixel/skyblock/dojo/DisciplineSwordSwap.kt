package me.ghluka.camel.module.modules.hypixel.skyblock.dojo

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.PlayerUtils
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.function.Predicate


class DisciplineSwordSwap : Module(SUBMODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Dojo Helper"
        @Exclude
        const val SUBMODULE = "Discipline Sword Swap"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = SUBMODULE, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $SUBMODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Swap speed (ms)", category = CATEGORY, subcategory = MODULE, min = 0F, max = 175F)
    var debounce: Int = 50
    @Exclude
    var timer = 0L

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return

        try {
            var target = mc.objectMouseOver.entityHit
            if (target == null)
                target = mc.pointedEntity

            if (System.currentTimeMillis() >= timer)
                swordSwap()
            if (target != null && target is EntityZombie && target.getEquipmentInSlot(0) != null) {
                timer = System.currentTimeMillis() + debounce
                //println("found zombie at ${System.currentTimeMillis()}")
            }
        }
        catch(_ : NullPointerException) {} // nonsense
    }

    fun swordSwap() {
        var target = mc.objectMouseOver.entityHit
        if (target == null)
            target = mc.pointedEntity

        if (target != null && target is EntityZombie && target.getEquipmentInSlot(4) != null) {
            //println("sword swapped at ${System.currentTimeMillis()}")
            val item = target.getEquipmentInSlot(4).item
            if (Items.diamond_helmet == item) {
                PlayerUtils.pickItem { stack -> stack?.item == Items.diamond_sword }
            } else if (Items.golden_helmet == item) {
                PlayerUtils.pickItem { stack -> stack?.item == Items.golden_sword }
            } else if (Items.iron_helmet == item) {
                PlayerUtils.pickItem { stack -> stack?.item == Items.iron_sword }
            } else if (Items.leather_helmet == item) {
                PlayerUtils.pickItem { stack -> stack?.item == Items.wooden_sword }
            }
        }
    }
}