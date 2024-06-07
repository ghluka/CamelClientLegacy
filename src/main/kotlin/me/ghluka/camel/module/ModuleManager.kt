package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.modules.combat.*
import me.ghluka.camel.module.modules.hud.*
import me.ghluka.camel.module.modules.render.*

import java.util.Collections;

class ModuleManager : Config(MainMod.MOD, MainMod.MODID + ".json") {
    val modules = ArrayList<Module>()
    lateinit var modulesList: ModulesList
    lateinit var targetHud: TargetHud

    init {
        Collections.addAll(modules,
            Reach(),
            Velocity(),
            Chams()
        )

        targetHud = TargetHud()
        modulesList = ModulesList()

        Collections.addAll(modules,
            targetHud
        )

        initialize()
    }

    override fun save() {
        modules.forEach() { mod ->
            mod.save()
        }
        modulesList.save()
    }
}