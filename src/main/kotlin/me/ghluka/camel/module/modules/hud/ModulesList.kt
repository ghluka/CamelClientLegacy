package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Page
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.PageLocation
import cc.polyfrost.oneconfig.hud.TextHud
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.modules.combat.LeftClicker

class ModulesList : me.ghluka.camel.module.Module(MODULE) {
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
        @Dropdown(name = "Caps Type", options = ["lower case", "Title Case", "UPPER CASE"])
        var capsType: Int = 1
        @Switch(name = "Remove spaces", category = CATEGORY, subcategory = MODULE, size = 1)
        var removeSpaces = true

        @Exclude
        val hidden = listOf(
            "Hilarity"
        )

        override fun getLines(lines: MutableList<String>?, example: Boolean) {
            if (lines == null || mc.thePlayer == null) {
                return
            }

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