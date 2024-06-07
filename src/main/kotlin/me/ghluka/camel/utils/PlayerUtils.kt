package me.ghluka.camel.utils

import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3


open class PlayerUtils {
    companion object {
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
                    )
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
                if (entityDistance < distance && entity is EntityLivingBase) {
                    return MovingObjectPosition(entity, hitVec)
                }
            }
            return null
        }
    }
}