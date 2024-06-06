package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayer
import kotlin.math.ceil

class TargetHud : me.ghluka.camel.module.Module("Target HUD") {
    @HUD(
        name = "Target HUD",
        category = "HUD",
        subcategory = "Target HUD"
    )
    var targetHud = TargetHud()

    init {
        initialize()
    }

    class TargetHud : BasicHud(false, 1920 - 140f, 1080 - 70f) {
        @Color(name = "Text Color")
        var color = OneColor(255, 255, 255)

        @Color(name = "Health Text Color")
        var healthColor = OneColor(255, 0, 0)

        @Dropdown(name = "Text Type", options = ["No Shadow", "Shadow", "Full Shadow"])
        var textType = 0

        @Slider(
            name = "Rotation",
            min = 0F,
            max = 360F,
        )
        var rotation = 0

        @Exclude
        private var nametagExtend = 0

        @Transient private var drawBackground = false
        @Transient var renderingNametag = false
            private set

        override fun shouldDrawBackground() = drawBackground

        override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
            if (drawBackground) return
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            drawBackground = true
            try {
                drawAll(matrices, example)
            } finally {
                drawBackground = false
            }

            var entity = mc.objectMouseOver.entityHit
            if (entity == null || entity !is EntityPlayer) {
                GlStateManager.disableDepth()
                GlStateManager.popMatrix()
                return
            }

            GlStateManager.color(1f, 1f, 1f, 1f)

            renderLiving(entity as EntityLiving, matrices, x, y, scale, rotation)

            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableRescaleNormal()
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
            GlStateManager.disableTexture2D()
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)

            TextRenderer.drawScaledString((entity as EntityPlayer).name, x + 36 * scale, y + 2 * scale, color.rgb, TextRenderer.TextType.toType(textType), scale)
            TextRenderer.drawScaledString(ceil((entity as EntityPlayer).health).toInt().toString() + " ❤", x + 36 * scale, y + 16 * scale, healthColor.rgb, TextRenderer.TextType.toType(textType), scale * 2f)

            GlStateManager.popMatrix()
        }

        private fun renderLiving(ent: EntityLiving, matrices: UMatrixStack?, x: Float, y: Float, scale: Float, rotation: Int) {
            GlStateManager.enableColorMaterial()
            GlStateManager.pushMatrix()

            GlStateManager.translate(x.toDouble() + (40 * scale), y.toDouble() + (107 + nametagExtend) * scale, 50.0)
            GlStateManager.scale(-(scale * 50), scale * 50, scale * 50)
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)

            val f = ent.renderYawOffset
            val f1 = ent.rotationYaw
            val f2 = ent.rotationPitch
            val f3 = ent.prevRotationYawHead
            val f4 = ent.rotationYawHead

            GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
            RenderHelper.enableStandardItemLighting()
            GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)

            val actualRotation = 360F - rotation
            ent.renderYawOffset = actualRotation
            ent.rotationYaw = actualRotation
            ent.rotationYawHead = ent.rotationYaw
            ent.prevRotationYawHead = ent.rotationYaw

            GlStateManager.translate(0.0f, 0.0f, 0.0f)
            GlStateManager.scale(0.5f, 0.5f, 0.5f)
            GlStateManager.translate(-1.0f, 2.2f, 0.0f)

            val rendermanager = mc.renderManager
            rendermanager.playerViewX = 0f
            rendermanager.setPlayerViewY(180.0f)
            rendermanager.isRenderShadow = false
            renderingNametag = true
            rendermanager.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
            renderingNametag = false
            rendermanager.isRenderShadow = true

            ent.renderYawOffset = f
            ent.rotationYaw = f1
            ent.rotationPitch = f2
            ent.prevRotationYawHead = f3
            ent.rotationYawHead = f4

            GlStateManager.popMatrix()
        }

        override fun getWidth(scale: Float, example: Boolean): Float = 120 * scale

        override fun getHeight(scale: Float, example: Boolean): Float = (50 + nametagExtend) * scale
    }
}