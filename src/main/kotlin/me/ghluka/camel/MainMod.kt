package me.ghluka.camel;

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import me.ghluka.camel.command.ExampleCommand
import me.ghluka.camel.module.ModuleManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    modid = MainMod.MODID,
    name = MainMod.NAME,
    version = MainMod.VERSION,
    clientSideOnly = true,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object MainMod {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    lateinit var moduleManager: ModuleManager

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent?) {
        moduleManager = ModuleManager()
        CommandManager.INSTANCE.registerCommand(ExampleCommand())
    }
}