package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.Notifications
import cc.polyfrost.oneconfig.utils.dsl.mc
import com.mojang.realmsclient.gui.ChatFormatting
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.modules.hypixel.skyblock.dojo.ForceAIO
import me.ghluka.camel.utils.ReflectionUtils
import me.ghluka.camel.utils.RenderUtils
import me.ghluka.camel.utils.SkyblockUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityItemFrame
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerFurnace
import net.minecraft.inventory.ContainerWorkbench
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.item.crafting.ShapelessRecipes
import net.minecraft.util.BlockPos
import net.minecraft.util.EntitySelectors
import net.minecraft.world.World
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe


class MurderMysteryAIO : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Murder Mystery AIO"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    @Info(text = "An All-In-One module for Hypixel's Murder Mystery (/l murder).", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Switch(name = "Murderer ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    var murdererESP: Boolean = true
    @Color(name = "Murderer Color", category = CATEGORY, subcategory = MODULE, size = 1)
    var murdererColor: OneColor = OneColor(java.awt.Color.red)

    @Switch(name = "Detective ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    var detectiveESP: Boolean = true
    @Color(name = "Detective Color", category = CATEGORY, subcategory = MODULE, size = 1)
    var detectiveColor: OneColor = OneColor(java.awt.Color.blue)

    @Switch(name = "Innocent ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    var innocentsESP: Boolean = false
    @Color(name = "Innocent Color", category = CATEGORY, subcategory = MODULE, size = 1)
    var innocentsColor: OneColor = OneColor(java.awt.Color.green)

    @Switch(name = "Bow ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    var bowESP: Boolean = true
    @Color(name = "Bow Color", category = CATEGORY, subcategory = MODULE, size = 1)
    var bowColor: OneColor = OneColor(135, 101, 38)

    @Switch(name = "Ingot ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    var ingotESP: Boolean = true
    @Color(name = "Ingot Color", category = CATEGORY, subcategory = MODULE, size = 1)
    var ingotColor: OneColor = OneColor(200, 194, 75)

    @Exclude
    val knives = listOf(
        Items.iron_sword, Items.stone_sword, Items.iron_shovel, Items.stick, Items.wooden_axe, Items.wooden_sword, Blocks.deadbush.getItem(null, null), Items.stone_shovel, Items.diamond_shovel, Items.quartz, Items.pumpkin_pie, Items.golden_pickaxe, Items.apple, Items.name_tag, Blocks.sponge.getItem(null, null), Items.carrot_on_a_stick, Items.bone, Items.carrot, Items.golden_carrot, Items.cookie, Items.diamond_axe, Blocks.red_flower.getItem(null, null), Items.prismarine_shard, Items.cooked_beef, Items.golden_sword, Items.diamond_sword, Items.diamond_hoe, Items.shears, Items.fish, Items.dye, Items.boat, Items.speckled_melon, Items.blaze_rod
    )

    @Exclude
    var murderers = mutableListOf<EntityPlayer>()
    @Exclude
    var detectives = mutableListOf<EntityPlayer>()
    @Exclude
    var inMurder = false

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onTick(e: TickEvent.ClientTickEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        try {
            mc.thePlayer.worldScoreboard?.let { co ->
                val objective = co.getObjectiveInDisplaySlot(1)
                if (objective != null && ChatFormatting.stripFormatting(objective.displayName) == "MURDER MYSTERY" && SkyblockUtils.hasLine("Innocents Left:")) {
                    this.inMurder = true
                    for (player in mc.theWorld.playerEntities) {
                        if (!murderers.contains(player) && !detectives.contains(player) && player.heldItem != null) {
                            if (detectives.size < 2 && player.heldItem!!.item == Items.bow) {
                                detectives.add(player)
                                Notifications.INSTANCE.send("Camel", "${player.name} is a detective!")
                            }

                            if (this.knives.contains(player.heldItem!!.item)) {
                                murderers.add(player)
                                Notifications.INSTANCE.send("Camel", "${player.name} is a murderer!")
                            }
                        }
                    }
                    return
                }

                this.inMurder = false
                murderers.clear()
                detectives.clear()
            }
        }
        catch (_: Exception) { }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return

        if (!inMurder) return
        for (entity: Entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityPlayer) {
                val player = entity
                if (!player.isPlayerSleeping && player != mc.thePlayer) {
                    if (murderers.contains(player) && murdererESP) {
                        RenderUtils.ree(player, murdererColor.rgb)
                    } else if (detectives.contains(player) && detectiveESP) {
                        RenderUtils.ree(player, detectiveColor.rgb)
                    } else if (innocentsESP) {
                        RenderUtils.ree(player, innocentsColor.rgb)
                    }
                }
            } else if (entity is EntityItem && entity.entityItem.item == Items.gold_ingot && ingotESP) {
                RenderUtils.ree(entity, ingotColor.rgb)
            } else if (bowESP && entity is EntityArmorStand && entity.getEquipmentInSlot(0) != null && entity.getEquipmentInSlot(0).item == Items.bow) {
                RenderUtils.ree(entity, bowColor.rgb)
            }
        }
    }
}