package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.renderer.TextRenderer
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import net.minecraft.client.renderer.GlStateManager

class Watermark : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Watermark"
        @Exclude
        const val CATEGORY = "HUD"
    }

    @HUD(name = MODULE, category = CATEGORY, subcategory = MODULE)
    var watermark = Watermark()

    init {
        initialize()
    }

    override fun save() {
        moduleEnabled = watermark.isEnabled
        super.save()
    }

    class Watermark : BasicHud(
        true,
        0f,
        0f,
        1f,
        false,
        false,
        8f,
        5f,
        5f,
        OneColor(0, 0, 0, 120),
        false,
        2f,
        OneColor(0, 0, 0)
    ) {
        @Color(name = "Watermark Color")
        var color = OneColor(100, 100, 255, 30F)

        @Color(name = "Build Color")
        var buildColor = OneColor(255, 255, 255)

        @Dropdown(name = "Text Type", options = ["No Shadow", "Shadow", "Full Shadow"])
        var textType = 1

        @Exclude
        private var nametagExtend = 0

        @Transient private var drawBackground = false
        @Transient var renderingNametag = false
            private set

        @Text(name = "Custom Build Text", category = CATEGORY, subcategory = MODULE,
            placeholder = "b${MainMod.VERSION}", secure = false, multiline = false)
        var buildText: String = ""

        override fun shouldDrawBackground() = drawBackground

        override fun draw(
            matrices: UMatrixStack?,
            x: Float,
            y: Float,
            scale: Float,
            example: Boolean
        ) {
            if (drawBackground) return
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            drawBackground = true
            try {
                drawAll(matrices, example)
            } finally {
                drawBackground = false
            }

            TextRenderer.drawScaledString(
                "Camel",
                x,
                y + 2 * scale,
                color.rgb,
                TextRenderer.TextType.toType(textType),
                scale * 1.5f
            )
            TextRenderer.drawScaledString(
                if (buildText == "") "b${MainMod.VERSION}" else buildText,
                x + 42 * scale,
                y + 1 * scale,
                buildColor.rgb,
                TextRenderer.TextType.toType(textType),
                scale
            )

            GlStateManager.popMatrix()
        }

        override fun getWidth(scale: Float, example: Boolean): Float {
            try {
                return (42 + TextRenderer.getStringWidth(if (buildText == "") "b${MainMod.VERSION}" else buildText)) * scale
            }
            catch (_ : NullPointerException) {
                return 1f
            }
        }

        override fun getHeight(scale: Float, example: Boolean): Float = 16 * scale
    }
}