package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.events.PlayerMoveEvent
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


open class RotationUtils {

    var startRot: Rotation? = null
    var endRot: Rotation? = null
    private var startTime: Long = 0
    var endTime: Long = 0

    var serverPitch = 0f
    var serverYaw = 0f
    var currentFakeYaw = 0f
    var currentFakePitch = 0f
    var done = true

    enum class RotationType {
        NORMAL,
        SERVER
    }


    var rotationType: RotationType? = null

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }


    class Rotation(var pitch: Float, var yaw: Float) {

        val value: Float
            get() = Math.abs(yaw) + Math.abs(pitch)

        override fun toString(): String {
            return "pitch=" + pitch +
                    ", yaw=" + yaw
        }
    }


    fun wrapAngleTo180(angle: Double): Double {
        return angle - Math.floor(angle / 360 + 0.5) * 360
    }

    fun wrapAngleTo180(angle: Float): Float {
        return (angle - Math.floor(angle / 360 + 0.5) * 360).toFloat()
    }

    fun fovToVec3(vec: Vec3): Float {
        val x = vec.xCoord - mc.thePlayer.posX
        val z = vec.zCoord - mc.thePlayer.posZ
        val yaw = Math.atan2(x, z) * 57.2957795
        return (yaw * -1.0).toFloat()
    }

    fun getRotation(from: Vec3, to: Vec3): Rotation? {
        val diffX = to.xCoord - from.xCoord
        val diffY = to.yCoord - from.yCoord
        val diffZ = to.zCoord - from.zCoord
        val dist = Math.sqrt(diffX * diffX + diffZ * diffZ)
        var pitch = -Math.atan2(dist, diffY).toFloat()
        var yaw = Math.atan2(diffZ, diffX).toFloat()
        pitch = wrapAngleTo180((pitch * 180f / Math.PI + 90) * -1).toFloat()
        yaw = wrapAngleTo180(yaw * 180 / Math.PI - 90).toFloat()
        return Rotation(pitch, yaw)
    }

    fun getRotation(vec3: Vec3): Rotation? {
        return getRotation(Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), vec3)
    }

    fun getRotation(block: BlockPos): Rotation? {
        return getRotation(Vec3(block.x + 0.5, block.y + 0.5, block.z + 0.5))
    }

    fun getRotation(entity: Entity): Rotation? {
        return getRotation(Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ))
    }

    fun getRotation(entity: Entity, offset: Vec3): Rotation? {
        return getRotation(Vec3(entity.posX + offset.xCoord, entity.posY + offset.yCoord, entity.posZ + offset.zCoord))
    }

    fun getNeededChange(startRot: Rotation, endRot: Rotation): Rotation {
        var yawDiff = wrapAngleTo180(endRot.yaw) - wrapAngleTo180(startRot.yaw)
        if (yawDiff <= -180) {
            yawDiff += 360f
        } else if (yawDiff > 180) {
            yawDiff -= 360f
        }
        return Rotation(endRot.pitch - startRot.pitch, yawDiff)
    }

    fun getVectorForRotation(pitch: Float, yaw: Float): Vec3? {
        val f2 = -MathHelper.cos(-pitch * 0.017453292f)
        return Vec3((MathHelper.sin(-yaw * 0.017453292f - 3.1415927f) * f2).toDouble(), MathHelper.sin(-pitch * 0.017453292f).toDouble(), (MathHelper.cos(-yaw * 0.017453292f - 3.1415927f) * f2).toDouble())
    }

    fun getLook(vec: Vec3): Vec3? {
        val diffX = vec.xCoord - mc.thePlayer.posX
        val diffY = vec.yCoord - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight())
        val diffZ = vec.zCoord - mc.thePlayer.posZ
        val dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()
        return getVectorForRotation((-(MathHelper.atan2(diffY, dist) * 180.0 / 3.141592653589793)).toFloat(), (MathHelper.atan2(diffZ, diffX) * 180.0 / 3.141592653589793 - 90.0).toFloat())
    }

    fun getNeededChange(endRot: Rotation): Rotation? {
        return getNeededChange(Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw), endRot)
    }

    fun getServerNeededChange(endRotation: Rotation): Rotation? {
        return if (endRot == null) getNeededChange(endRotation) else getNeededChange(endRot!!, endRotation)
    }

    private fun interpolate(start: Float, end: Float): Float {
        return (end - start) * easeOutCubic(((System.currentTimeMillis() - startTime).toFloat() / (endTime - startTime)).toDouble()) + start
    }

    fun easeOutCubic(number: Double): Float {
        return Math.max(0.0, Math.min(1.0, 1 - Math.pow(1 - number, 3.0))).toFloat()
    }

    fun smoothLookRelative(rotation: Rotation, time: Long) {
        smoothLookRelative(rotation, time, false)
    }

    fun smoothLookRelative(rotation: Rotation, time: Long, yawOnly: Boolean) {
        rotationType = RotationType.NORMAL
        done = false
        startRot = Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw)
        endRot = if (yawOnly) {
            Rotation(rotation.pitch, startRot!!.yaw + rotation.yaw)
        } else {
            Rotation(startRot!!.pitch + rotation.pitch, startRot!!.yaw + rotation.yaw)
        }
        startTime = System.currentTimeMillis()
        endTime = System.currentTimeMillis() + time
    }

    fun smoothLook(rotation: Rotation, time: Long) {
        rotationType = RotationType.NORMAL
        done = false
        startRot = Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw)
        val neededChange = getNeededChange(startRot!!, rotation)
        endRot = Rotation(startRot!!.pitch + neededChange.pitch, startRot!!.yaw + neededChange.yaw)
        startTime = System.currentTimeMillis()
        endTime = System.currentTimeMillis() + time
    }

    fun smartSmoothLook(rotation: Rotation, msPer180: Int) {
        val rotationDifference = wrapAngleTo180(Math.max(
                Math.abs(rotation.pitch - mc.thePlayer.rotationPitch),
                Math.abs(rotation.yaw - mc.thePlayer.rotationYaw)
        ))
        smoothLook(rotation, (rotationDifference / 180 * msPer180).toInt().toLong())
    }

    fun serverSmoothLookRelative(rotation: Rotation, time: Long) {
        rotationType = RotationType.SERVER
        done = false
        if (currentFakePitch == 0f) currentFakePitch = mc.thePlayer.rotationPitch
        if (currentFakeYaw == 0f) currentFakeYaw = mc.thePlayer.rotationYaw
        startRot = Rotation(currentFakePitch, currentFakeYaw)
        endRot = Rotation(startRot!!.pitch + rotation.pitch, startRot!!.yaw + rotation.yaw)
        startTime = System.currentTimeMillis()
        endTime = System.currentTimeMillis() + time
    }

    fun serverSmoothLook(rotation: Rotation, time: Long) {
        rotationType = RotationType.SERVER
        done = false
        if (currentFakePitch == 0f) currentFakePitch = mc.thePlayer.rotationPitch
        if (currentFakeYaw == 0f) currentFakeYaw = mc.thePlayer.rotationYaw
        startRot = Rotation(currentFakePitch, currentFakeYaw)
        val neededChange = getNeededChange(startRot!!, rotation)
        endRot = Rotation(startRot!!.pitch + neededChange.pitch, startRot!!.yaw + neededChange.yaw)
        startTime = System.currentTimeMillis()
        endTime = System.currentTimeMillis() + time
    }

    fun updateServerLookResetting() {
        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationYaw = interpolate(startRot!!.yaw, endRot!!.yaw)
            mc.thePlayer.rotationPitch = interpolate(startRot!!.pitch, endRot!!.pitch)
            currentFakeYaw = mc.thePlayer.rotationYaw
            currentFakePitch = mc.thePlayer.rotationPitch
        } else {
            if (!done) {
                mc.thePlayer.rotationYaw = endRot!!.yaw
                mc.thePlayer.rotationPitch = endRot!!.pitch
                currentFakeYaw = mc.thePlayer.rotationYaw
                currentFakePitch = mc.thePlayer.rotationPitch
                reset()
            }
        }
    }

    fun updateServerLook() {
        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationYaw = interpolate(startRot!!.yaw, endRot!!.yaw)
            mc.thePlayer.rotationPitch = interpolate(startRot!!.pitch, endRot!!.pitch)
            currentFakeYaw = mc.thePlayer.rotationYaw
            currentFakePitch = mc.thePlayer.rotationPitch
        } else {
            if (!done) {
                mc.thePlayer.rotationYaw = endRot!!.yaw
                mc.thePlayer.rotationPitch = endRot!!.pitch
                currentFakeYaw = mc.thePlayer.rotationYaw
                currentFakePitch = mc.thePlayer.rotationPitch
            }
        }
    }

    fun look(rotation: Rotation) {
        mc.thePlayer.rotationPitch = rotation.pitch
        mc.thePlayer.rotationYaw = rotation.yaw
    }

    fun reset() {
        done = true
        startRot = null
        rotationType = null
        endRot = null
        startTime = 0
        endTime = 0
        currentFakeYaw = 0f
        currentFakePitch = 0f
    }

    @SubscribeEvent
    fun onRenderWorld(event: RenderWorldLastEvent?) {
        if (rotationType != RotationType.NORMAL) return
        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationPitch = interpolate(startRot!!.pitch, endRot!!.pitch)
            mc.thePlayer.rotationYaw = interpolate(startRot!!.yaw, endRot!!.yaw)
        } else {
            if (!done) {
                mc.thePlayer.rotationYaw = endRot!!.yaw
                mc.thePlayer.rotationPitch = endRot!!.pitch
                reset()
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onUpdatePre(pre: PlayerMoveEvent.Pre?) {
        serverPitch = mc.thePlayer.rotationPitch
        serverYaw = mc.thePlayer.rotationYaw
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onUpdatePost(post: PlayerMoveEvent.Post?) {
        mc.thePlayer.rotationPitch = serverPitch
        mc.thePlayer.rotationYaw = serverYaw
    }
}