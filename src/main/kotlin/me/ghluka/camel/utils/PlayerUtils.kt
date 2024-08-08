package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import java.lang.reflect.Field
import java.lang.reflect.Method


open class PlayerUtils {
    companion object {
        fun swingHand(objectMouseOver: MovingObjectPosition?) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit == null)
                mc.thePlayer.swingItem()
        }

        fun rightClick() {
            if (!ReflectionUtils.invoke(mc, "func_147121_ag"))
                ReflectionUtils.invoke(mc, "rightClickMouse")
        }

        fun leftClick() {
            if (!ReflectionUtils.invoke(mc, "func_147116_af"))
                ReflectionUtils.invoke(mc, "clickMouse")
        }

        fun middleClick() {
            if (!ReflectionUtils.invoke(mc, "func_147112_ai"))
                ReflectionUtils.invoke(mc, "middleClickMouse")
        }

        fun getMouseOver(distance: Double, expand: Double): MovingObjectPosition? {
            if (Minecraft.getMinecraft().renderViewEntity != null && Minecraft.getMinecraft().theWorld != null) {
                var entity: Entity? = null
                val eyes = Minecraft.getMinecraft().renderViewEntity.getPositionEyes(1.0f)
                val look = Minecraft.getMinecraft().renderViewEntity.getLook(1.0f)
                val extendedLook = eyes.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance)
                var hitVec: Vec3? = null
                val entities: List<*> = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(
                    Minecraft.getMinecraft().renderViewEntity,
                    Minecraft.getMinecraft().renderViewEntity.entityBoundingBox.addCoord(
                        look.xCoord * distance,
                        look.yCoord * distance,
                        look.zCoord * distance
                    ).expand(1.0, 1.0, 1.0)
                )
                var entityDistance = distance
                for (i in entities.indices) {
                    var tempDistance: Double = 0.0
                    val currentEntity = entities[i] as Entity
                    if (!currentEntity.canBeCollidedWith()) continue
                    val hitboxSize = currentEntity.collisionBorderSize
                    var hitbox = currentEntity.entityBoundingBox.expand(hitboxSize.toDouble(), hitboxSize.toDouble(), hitboxSize.toDouble())
                    hitbox = hitbox.expand(expand, expand, expand)
                    val intercept = hitbox.calculateIntercept(eyes, extendedLook)
                    if (hitbox.isVecInside(eyes)) {
                        if (!(0.0 < entityDistance) && entityDistance != 0.0) continue
                        entity = currentEntity
                        hitVec = if (intercept == null) eyes else intercept.hitVec
                        entityDistance = 0.0
                        continue
                    }
                    if (intercept == null || !((eyes.distanceTo(intercept.hitVec)
                            .also { tempDistance = it }) < entityDistance) && entityDistance != 0.0
                    ) continue
                    if (currentEntity === Minecraft.getMinecraft().renderViewEntity.ridingEntity && !entity!!.canRiderInteract()) {
                        if (entityDistance != 0.0) continue
                        entity = currentEntity
                        hitVec = intercept.hitVec
                        continue
                    }
                    entity = currentEntity
                    hitVec = intercept.hitVec
                    entityDistance = tempDistance
                }
                if (entityDistance <= distance && entity is EntityLivingBase) {
                    return MovingObjectPosition(entity, hitVec)
                }
            }
            return null
        }
    }
}

class ReflectionUtils {
    companion object {
        fun invoke(obj: Any, methodName: String): Boolean {
            try {
                val method: Method = obj.javaClass.getDeclaredMethod(methodName, *arrayOfNulls(0))
                method.setAccessible(true)
                method.invoke(obj, arrayOfNulls<Any>(0))
                return true
            } catch (exception: Exception) {
                return false
            }
        }

        fun field(obj: Any, name: String): Any? {
            try {
                val field: Field = obj.javaClass.getDeclaredField(name)
                field.setAccessible(true)
                return field.get(obj)
            } catch (exception: Exception) {
                return null
            }
        }

        fun asField(obj: Any, name: String): Field? {
            try {
                val field: Field = obj.javaClass.getDeclaredField(name)
                field.setAccessible(true)
                return field
            } catch (exception: Exception) {
                return null
            }
        }
    }
}
