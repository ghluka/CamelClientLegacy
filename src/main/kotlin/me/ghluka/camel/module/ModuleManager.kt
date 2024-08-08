package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.modules.combat.*
import me.ghluka.camel.module.modules.hud.*
import me.ghluka.camel.module.modules.hypixel.arcade.*
import me.ghluka.camel.module.modules.hypixel.skyblock.*
import me.ghluka.camel.module.modules.misc.*
import me.ghluka.camel.module.modules.player.*
import me.ghluka.camel.module.modules.render.*

import java.util.Collections;

class ModuleManager : Config(MainMod.MOD, MainMod.MODID + ".json") {
    val modules = ArrayList<Module>()
    lateinit var modulesList: ModulesList
    lateinit var targetHud: TargetHud

    init {
        Collections.addAll(modules,
                /* Combat */
                Reach(),
                Velocity(),
                Hitboxes(),
        )
        /* HUD */
        targetHud = TargetHud()
        modulesList = ModulesList()
        Collections.addAll(modules,
            targetHud
        )
        Collections.addAll(modules,
                /* Hypixel Arcade */
                AnvilESP(),
                AvalancheESP(),
                HighGroundFences(),
                JigsawRushTriggerbot(),
                NoBlizzard(),
                /* Hypixel Skyblock */
                AutoJax(),
                FrozenTreasuresESP(),
                /* Misc */
                Hilarity(),
                /* Player */
                BackAndForth(),
                /* Render */
                Chams(),
        )

        initialize()
    }

    fun getModuleByName(name: String): Module? {
        modules.forEach() { mod ->
            if (mod.moduleName == name)
                return mod
        }
        return null
    }

    override fun save() {
        modules.forEach() { mod ->
            mod.save()
        }
        modulesList.save()
    }
}