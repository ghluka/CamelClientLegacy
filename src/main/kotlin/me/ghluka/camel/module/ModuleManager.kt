package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.SubConfig
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.modules.render.Chams
import me.ghluka.camel.module.modules.render.TargetHud

class ModuleManager : Config(MainMod.MOD, MainMod.MODID + ".json") {
    @SubConfig
    var chams = Chams()
    @SubConfig
    var targetHud = TargetHud()

    init {
        initialize()
    }
}