package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11


open class RenderUtils {

    companion object {
        fun re(bp: BlockPos?, color: Int) {
            if (bp == null) {
                return
            }
            val x: Double = bp.x.toDouble() - mc.getRenderManager().viewerPosX
            val y: Double = bp.y.toDouble() - mc.getRenderManager().viewerPosY
            val z: Double = bp.z.toDouble() - mc.getRenderManager().viewerPosZ
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