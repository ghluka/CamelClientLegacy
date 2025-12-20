package me.ghluka.camel.module.modules.hypixel.skyblock

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import com.mojang.authlib.properties.Property
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EntitySelectors
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class AntiGoon : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Anti Goon"
        @Exclude
        const val CATEGORY = "Hypixel Skyblock"
    }

    @Exclude
    @Info(text = "Prevents you from going to Aura's Prison of the Bereft when you punch the goons.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable ${MODULE}", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()
    
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
            for (entity in mc.theWorld.getEntities(EntityPlayer::class.java, EntitySelectors.selectAnything)) {
                try {
                    val profile = entity.gameProfile
                    val textureProp: Property? = profile.properties.get("textures").iterator().next()

                    val signature: String? = textureProp?.signature

                    if (signature == "LFZCl7wdKzpKLWB9hCtSlBcTdOVmTxSegt8tsNjkFG4WakWapnkIn7I2E5+5v63jisX+xE9/iTC+i/uAN/4ehfPVBgXKW22trIWQDFU3F4mVV60Rb3brYvoI1INAUP+zxZJEDW/HwtWzcEisfu8VorjMSUOTjOwrM5V41ueF8ZMTg4e2wmZSOWrBQ4boIGYX0KZzSvMzM1g03vC4kMJHDeKP9BLku5mcJgcY1wfNXPXLShenSB6mnUgi0Hm1i7xRwD8z+iIyaq15kFk8b0vuJnPqP9q+a9GXPF886Cad1kmCEOLfFtmECC06UN1TBy+uu1vXh+7a+0NvUWVdWL6p6hnlmAG+JTeFYL/bZNFcAMnXXaNJUWSEc9zbcYBetl0NFjzRRKGf/XKkEo1iiYd9Izc+OO48rhu9kNR7+QkAmN1PfrTL0v0iA1EjejnGnzadyi/HEXW12bX3UqwgtRzZIWGBnV37ZQv0mMx53R+ya/knDn/m44TytiPFsok/aP5mQqinh9ANla3jNYyogUXhs/pXJb0qGA0lRDTwpCL/Z5o3CPmaTeQiNS6Taz0d/EP11i2jf2H6TpNJxJ4LuwK5RNDvtCIKq6DLbq0PI5MvU/s9dLgbl6Hr1qrYZz2uheoweTQUzYeNr9SlaqXCbWIoqp+3oj1qaE/VZqQUKHfqRPM=") {
                        print("goon detected")
                        entity.isDead = true
                    }
                } catch (x: Exception) {
                }
            }
            for (entity in mc.theWorld.getEntities(EntityArmorStand::class.java, EntitySelectors.selectAnything)) {
                try {
                    if (entity.name == "Goon") {
                        entity.posY = -100.0
                        entity.prevPosY = -100.0
                    }
                } catch (x: Exception) {
                }
            }
        }
    }
}