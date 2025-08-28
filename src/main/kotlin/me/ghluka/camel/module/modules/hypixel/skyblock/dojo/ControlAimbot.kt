package me.ghluka.camel.module.modules.hypixel.skyblock.dojo

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.cos
import kotlin.math.sin


class ControlAimbot : Module(SUBMODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Dojo Helper"
        @Exclude
        const val SUBMODULE = "Control Aimbot"
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

    @Exclude
    var current: EntitySkeleton? = null

    @Slider(name = "Offset", category = CATEGORY, subcategory = MODULE, min = 0F, max = 3F)
    var offset = 2F

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
            current = null
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return

        if (current == null)
            current = closestSkele()
        if (current != null &&
            (!(current!!.posX >= -224-MainMod.dojoUtils.defaultSpawn.x+MainMod.dojoUtils.currentSpawn.x && current!!.posX <= -190-MainMod.dojoUtils.defaultSpawn.x+MainMod.dojoUtils.currentSpawn.x &&
            current!!.posZ >= -615-MainMod.dojoUtils.defaultSpawn.z+MainMod.dojoUtils.currentSpawn.z && current!!.posZ <= -581-MainMod.dojoUtils.defaultSpawn.z+MainMod.dojoUtils.currentSpawn.z) ||
            current!!.isDead || current!!.isInvisible)) {
            current = null
        }

        if (!MainMod.rotationUtils.done) return

        if (current != null) {
            val rot = MainMod.rotationUtils.getRotation(getLooking(current!!).addVector(0.0, 1.5, 0.0))
            if (rot != null) {
                MainMod.rotationUtils.smoothLook(rot, 50)
            }
        }
    }

    fun getLooking(entity: EntityLivingBase): Vec3 {
        val yaw = entity.rotationYaw
        val rad = Math.toRadians(yaw.toDouble())

        val x = -sin(rad)
        val z = cos(rad)

        return Vec3(
            entity.posX + x * offset,
            entity.posY,
            entity.posZ + z * offset
        )
    }

    fun closestSkele(): EntitySkeleton? {
        if (mc.theWorld == null || mc.thePlayer == null) return null
        var closest: EntitySkeleton? = null
        var closestDist = Double.Companion.MAX_VALUE
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntitySkeleton) {
                val skeleton = entity
                if (skeleton.skeletonType == 1 && skeleton.posX >= -224-MainMod.dojoUtils.defaultSpawn.x+MainMod.dojoUtils.currentSpawn.x && skeleton.posX <= -190-MainMod.dojoUtils.defaultSpawn.x+MainMod.dojoUtils.currentSpawn.x &&
                    skeleton.posZ >= -615-MainMod.dojoUtils.defaultSpawn.z+MainMod.dojoUtils.currentSpawn.z && skeleton.posZ <= -581-MainMod.dojoUtils.defaultSpawn.z+MainMod.dojoUtils.currentSpawn.z && !skeleton.isDead && !skeleton.isInvisible) {
                    val dist = mc.thePlayer.getDistanceSqToEntity(skeleton)
                    if (dist < closestDist) {
                        closestDist = dist
                        closest = skeleton
                    }
                }
            }
        }
        return closest
    }
}