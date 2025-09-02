package me.ghluka.camel.module.config

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.renderer.font.Font
import cc.polyfrost.oneconfig.renderer.font.Fonts
import me.ghluka.camel.module.Module

class Font : Module("Font") {

    @Dropdown(name = "Font family", category = "HUD", subcategory = "Font Selector", size = 1,
        options = ["Inter", "Minecraft"])
    var font = 0
    @Exclude
    companion object {
        fun stringToFont(name: Int, bold: Boolean = false): Font {
            when (name) {
                0 -> {
                    return if (bold) Fonts.BOLD else Fonts.REGULAR
                }
            }
            return Fonts.MINECRAFT_REGULAR //minecraft bold is ugly
            //return if (bold) Fonts.MINECRAFT_BOLD else Fonts.MINECRAFT_REGULAR
        }
    }

    init {
        initialize()
    }
}