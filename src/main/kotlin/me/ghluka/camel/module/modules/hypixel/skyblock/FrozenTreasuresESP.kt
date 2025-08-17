package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.PageLocation
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.command.commands.Camel
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.BlockPos
import net.minecraft.util.EntitySelectors
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color


class FrozenTreasuresESP : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Frozen Treasures ESP"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = "Renders an ESP over all frozen treasures", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Frozen Treasures ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @cc.polyfrost.oneconfig.config.annotations.Color(name = "ESP color", category = CATEGORY, subcategory = MODULE, size = 1)
    var espColor: OneColor = OneColor(Color.black)

    @Page(category = CATEGORY, subcategory = MODULE, name = "Frozen Treasures filters", location = PageLocation.BOTTOM)
    var frozenTreasuresPage: FrozenTreasuresPage = FrozenTreasuresPage()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer != null && mc.theWorld != null) {
            for (entity in mc.theWorld.getEntities(EntityArmorStand::class.java, EntitySelectors.selectAnything)) {
                try {
                    var render = false

                    if (isEntityHoldingItemByName(entity, "Packed Ice") && frozenTreasuresPage.packedIceESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Enchanted Ice") && frozenTreasuresPage.enchantedIceESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Enchanted Packed Ice") && frozenTreasuresPage.enchantedPackedIceESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Ice Bait") && frozenTreasuresPage.iceBaitESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Glowy Chum Bait") && frozenTreasuresPage.glowyChumBaitESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Glacial Fragment") && frozenTreasuresPage.glacialFragmentESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "White Gift") && frozenTreasuresPage.whiteGiftESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Green Gift") && frozenTreasuresPage.greenGiftESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Red Gift") && frozenTreasuresPage.redGiftESP)
                        render = true
                    else if (isEntityHoldingItemByName(entity, "Glacial Talisman") && frozenTreasuresPage.glacialTalismanESP)
                        render = true

                    if (render) {
                        RenderUtils.re(BlockPos(entity.posX, entity.posY + 2, entity.posZ), espColor.rgb)
                    }
                } catch (x: NullPointerException) {
                }
            }
        }
    }

    private fun isEntityHoldingItemByName(entity: EntityArmorStand, searchName: String): Boolean {
        val itemStack = entity.getCurrentArmor(3)
        if (itemStack != null && itemStack.serializeNBT().getCompoundTag("tag") != null && itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("display") != null && itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTag("Name") != null) {
            val name = itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTag("Name").toString().replace("\"", "")
            return name.contains(searchName)
        }
        return false
    }
}
class FrozenTreasuresPage {
    @Switch(name = "Packed Ice ESP", size = 1)
    var packedIceESP: Boolean = true

    @Switch(name = "Enchanted Ice ESP", size = 1)
    var enchantedIceESP: Boolean = true

    @Switch(name = "Enchanted Packed Ice ESP", size = 1)
    var enchantedPackedIceESP: Boolean = true

    @Switch(name = "Ice Bait ESP", size = 1)
    var iceBaitESP: Boolean = true

    @Switch(name = "Glowy Chum Bait ESP", size = 1)
    var glowyChumBaitESP: Boolean = true

    @Switch(name = "Glacial Fragment ESP", size = 1)
    var glacialFragmentESP: Boolean = true

    @Switch(name = "White Gift ESP", size = 1)
    var whiteGiftESP: Boolean = true

    @Switch(name = "Green Gift ESP", size = 1)
    var greenGiftESP: Boolean = true

    @Switch(name = "Red Gift ESP", size = 1)
    var redGiftESP: Boolean = true

    @Switch(name = "Glacial Talisman ESP", size = 1)
    var glacialTalismanESP: Boolean = true
}