package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.mixin.accessors.PlayerControllerAccessor
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.util.Vec3
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import java.util.function.Predicate


open class PlayerUtils {
    companion object {
        fun swingHand(objectMouseOver: MovingObjectPosition?) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit == null)
                mc.thePlayer.swingItem()
        }

        fun leftClick() {
            ReflectionUtils.invoke(mc, "func_147116_af")
            ReflectionUtils.invoke(mc, "clickMouse")
        }

        fun rightClick() {
            ReflectionUtils.invoke(mc, "func_147121_ag")
            ReflectionUtils.invoke(mc, "rightClickMouse")
        }

        fun middleClick() {
            ReflectionUtils.invoke(mc, "func_147112_ai")
            ReflectionUtils.invoke(mc, "middleClickMouse")
        }

        fun getReachMouseOver(distance: Double, expand: Double): MovingObjectPosition? {
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

        fun swapToSlot(slot: Int) {
            mc.thePlayer.inventory.currentItem = slot
            syncHeldItem()
        }

        fun getHotbar(predicate: Predicate<ItemStack?>?): Int {
            if (predicate == null) return -1
            for (i in 0..8) {
                val stack: ItemStack? = mc.thePlayer.inventory.getStackInSlot(i)
                if (stack != null && predicate.test(stack)) return i
            }
            return -1
        }

        fun syncHeldItem() {
            val slot: Int = mc.thePlayer.inventory.currentItem
            val controller: PlayerControllerAccessor = mc.playerController as PlayerControllerAccessor
            if (slot != controller.getCurrentPlayerItem()) {
                controller.setCurrentPlayerItem(slot)
                mc.netHandler.networkManager.sendPacket(C09PacketHeldItemChange(slot))
            }
        }
    }
}