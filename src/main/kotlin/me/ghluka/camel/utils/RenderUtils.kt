package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.mixin.accessors.MinecraftAccessor
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11


open class RenderUtils {

    companion object {
        fun re(bp: BlockPos?, color: Int) {
            if (bp == null) {
                return
            }
            val x: Double = bp.x.toDouble() - mc.renderManager.viewerPosX
            val y: Double = bp.y.toDouble() - mc.renderManager.viewerPosY
            val z: Double = bp.z.toDouble() - mc.renderManager.viewerPosZ
            GL11.glBlendFunc(770, 771)
            GL11.glEnable(3042)
            GL11.glLineWidth(2.0f)
            GL11.glDisable(3553)
            GL11.glDisable(2929)
            GL11.glDepthMask(false)
            val a = (color shr 24 and 0xFF).toFloat() / 255.0f
            val r = (color shr 16 and 0xFF).toFloat() / 255.0f
            val g = (color shr 8 and 0xFF).toFloat() / 255.0f
            val b = (color and 0xFF).toFloat() / 255.0f
            GL11.glColor4d(r.toDouble(), g.toDouble(), b.toDouble(), a.toDouble())
            RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0))
            dbb(AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), r, g, b)
            GL11.glEnable(3553)
            GL11.glEnable(2929)
            GL11.glDepthMask(true)
            GL11.glDisable(3042)
        }

        fun ree(entity: Entity, color: Int) {
            val partialTicks = (mc as MinecraftAccessor).timer.renderPartialTicks
            val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
            val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
            val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ

            val bb: AxisAlignedBB = if (entity is EntityArmorStand) {
                val size = 0.25
                val headHeight = 1.5
                AxisAlignedBB(
                    x - size, y + headHeight - size,
                    z - size, x + size, y + headHeight + size,
                    z + size
                )
            } else {
                entity.entityBoundingBox.offset(
                    x - entity.posX,
                    y - entity.posY,
                    z - entity.posZ
                )
            }

            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glLineWidth(2.0f)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(false)

            val a = (color shr 24 and 0xFF) / 255f
            val r = (color shr 16 and 0xFF) / 255f
            val g = (color shr 8 and 0xFF) / 255f
            val b = (color and 0xFF) / 255f
            GL11.glColor4f(r, g, b, a)

            RenderGlobal.drawSelectionBoundingBox(bb)

            val ts = Tessellator.getInstance()
            val vb = ts.worldRenderer
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR)

            vb.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 0.25f).endVertex()

            vb.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 0.25f).endVertex()

            vb.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, 0.25f).endVertex()

            vb.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 0.25f).endVertex()

            vb.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, 0.25f).endVertex()

            vb.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            vb.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, 0.25f).endVertex()
            ts.draw()

            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(true)
            GL11.glDisable(GL11.GL_BLEND)
        }

        fun dbb(abb: AxisAlignedBB, r: Float, g: Float, b: Float) {
            val a = 0.25f
            val ts = Tessellator.getInstance()
            val vb = ts.worldRenderer
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            ts.draw()
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            ts.draw()
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            ts.draw()
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            ts.draw()
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            ts.draw()
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex()
            vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex()
            ts.draw()
        }
    }
}