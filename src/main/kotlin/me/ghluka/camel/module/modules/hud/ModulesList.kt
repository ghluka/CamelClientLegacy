package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.TextHud
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module

class ModulesList : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Modules List"
        @Exclude
        const val CATEGORY = "HUD"
    }

    @HUD(name = MODULE, category = CATEGORY, subcategory = MODULE)
    var modulesList = ModulesList()

    init {
        initialize()
    }

    override fun save() {
        moduleEnabled = modulesList.isEnabled
        super.save()
    }

    class ModulesList : TextHud(
        true,
        0f,
        20f,
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
        @Dropdown(name = "Caps Type", options = ["lower case", "Title Case", "UPPER CASE"])
        var capsType: Int = 0
        @Switch(name = "Remove spaces", category = CATEGORY, subcategory = MODULE, size = 1)
        var removeSpaces = false

        @Dropdown(name = "Sort By", options = ["Order", "Alphabetical", "Length"])
        var sortBy: Int = 2
        @Exclude
        var lastSortBy: Int = 0
        @Switch(name = "Reverse sort", category = CATEGORY, subcategory = MODULE, size = 1)
        var reversed = true
        @Exclude
        var lastReversed = false

        init {
            color = OneColor(25, 100, 255, 30F)
            textType = 1
        }

        @Exclude
        val hidden = listOf(
            "Hilarity",
            "Watermark"
        )

        override fun getLines(lines: MutableList<String>?, example: Boolean) {
            if (lines == null || mc.thePlayer == null) {
                return
            }

            val modules = MainMod.moduleManager.modules
            if (lastSortBy != sortBy) {
                if (sortBy == 0)
                    modules.sortBy { MainMod.moduleManager.moduleNames.indexOf(it.moduleName) }
                else if (sortBy == 1)
                    modules.sortBy { it.moduleName }
                else if (sortBy == 2)
                    modules.sortBy { TextRenderer.getStringWidth(it.moduleName) }
            }
            if (lastReversed != reversed) {
                modules.reverse()
            }
            lastSortBy = sortBy
            lastReversed = reversed

            MainMod.moduleManager.modules.forEach { mod: Module ->
                if (mod.moduleEnabled && !hidden.contains(mod.moduleName)) {
                    var moduleName = mod.moduleName

                    if (capsType == 0)
                        moduleName = moduleName.lowercase()
                    else if (capsType == 2)
                        moduleName = moduleName.uppercase()

                    if (removeSpaces)
                        moduleName = moduleName.replace(" ", "").replace("-", "")

                    lines.add(moduleName)
                }
            }
        }
    }
}