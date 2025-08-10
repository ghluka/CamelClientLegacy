package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.hud.TextHud
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.modules.player.BackAndForth

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
    @Switch(name = "Show spaces in module names", category = CATEGORY, subcategory = MODULE, size = 1)
    var showSpaces: Boolean = true

    init {
        initialize()
    }

    override fun save() {
        moduleEnabled = modulesList.isEnabled
        super.save()
    }

    class ModulesList : TextHud(true) {
        override fun getLines(lines: MutableList<String>?, example: Boolean) {
            if (lines == null || mc.thePlayer == null) {
                return
            }

            MainMod.moduleManager.modules.forEach { mod: Module ->
                if (mod.moduleEnabled && mod.moduleName != "Hilarity")
                    lines.add(mod.moduleName)
            }
        }
    }
}