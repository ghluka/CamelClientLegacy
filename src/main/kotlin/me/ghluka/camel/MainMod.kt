package me.ghluka.camel;

import cc.polyfrost.oneconfig.config.data.ModType
import me.ghluka.camel.command.CommandManager
import me.ghluka.camel.module.ModuleManager
import me.ghluka.camel.utils.RotationUtils
import net.minecraft.client.Minecraft
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

    val MOD = cc.polyfrost.oneconfig.config.data.Mod(NAME, ModType.UTIL_QOL, "/logo.png", 64, 64)

    @kotlin.jvm.JvmField
    var mc: Minecraft = Minecraft.getMinecraft()

    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    @kotlin.jvm.JvmField
    var rotationUtils: RotationUtils = RotationUtils()

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent?) {
        moduleManager = ModuleManager()
        commandManager = CommandManager()
    }
}