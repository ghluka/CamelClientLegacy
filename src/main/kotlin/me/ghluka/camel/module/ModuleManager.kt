package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.config.Font
import me.ghluka.camel.module.modules.combat.*
import me.ghluka.camel.module.modules.dev.*
import me.ghluka.camel.module.modules.hud.*
import me.ghluka.camel.module.modules.world.*
import me.ghluka.camel.module.modules.hypixel.arcade.*
import me.ghluka.camel.module.modules.hypixel.skyblock.*
import me.ghluka.camel.module.modules.hypixel.skyblock.dojo.*
import me.ghluka.camel.module.modules.misc.*
import me.ghluka.camel.module.modules.movement.*
import me.ghluka.camel.module.modules.player.*
import me.ghluka.camel.module.modules.render.*

import java.util.Collections;

class ModuleManager : Config(MainMod.MOD, MainMod.MODID + ".json") {
    val modules = ArrayList<Module>()
    var moduleNames = ArrayList<String>()

    lateinit var font: Font

    lateinit var modulesList: ModulesList
    lateinit var targetHud: TargetHud

    init {
        Collections.addAll(modules,
            /* Combat */
            LeftClicker(),
            RightClicker(),
            Hitboxes(),
            NoDelay(),
            Reach(),
            Velocity(),
        )
        /* HUD */
        font = Font()
        Collections.addAll(modules,CustomMenu())
        modulesList = ModulesList()
        targetHud = TargetHud()
        Collections.addAll(modules,
            targetHud,
            Watermark()
        )
        Collections.addAll(modules,
            /* Hypixel Arcade */
            AnimalSlaughterAIO(),
            AnvilESP(),
            AvalancheESP(),
            HighGroundFences(),
            HoleInTheWallAIO(),
            JigsawRushTriggerbot(),
            MurderMysteryAIO(),
            NoBlizzard(),
            PropHuntESP(),
            SpiderMazePathfinder(),
            WorkshopAIO(),
            /* Hypixel Skyblock */
            AutoJax(),
            /// dojo qol ///
            ForceAIO(),
            StaminaESP(),
            MasteryAIO(),
            DisciplineSwordSwap(),
            ControlAimbot(),
            /// end of dojo qol///
            FrozenTreasuresESP(),
            GardenAntivoid(),
            MithrilLockCam(),
            PestESP(),
            PowderChestAura(),
            /* Movement */
            Flight(),
            NoFall(),
            Parkour(),
            SafeWalk(),
            /* Player */
            AutoTool(),
            BackAndForth(),
            InfParty(),
            SpamBypass(),
            /* Render */
            Chams(),
            PlayerESP(),
            /* World */
            PSTWaypoint(),
            /* Misc */
            Hilarity(),
            /* Dev */
            CopyNBT(),
        )

        moduleNames = modules.map { it.moduleName } as ArrayList<String>
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
        font.save()
    }
}