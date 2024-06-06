package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.modules.render.Chams
import me.ghluka.camel.module.modules.render.ModulesList
import me.ghluka.camel.module.modules.render.TargetHud

import java.util.Collections;

class ModuleManager : Config(MainMod.MOD, MainMod.MODID + ".json") {
    val modules = ArrayList<Module>()
    var targetHud = TargetHud()

    init {
        Collections.addAll(modules,
            Chams(),
            targetHud
        )
        initialize()
    }

    var moduleList = ModulesList()
}