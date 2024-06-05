package me.ghluka.camel.command;

import me.ghluka.camel.MainMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in ExampleMod.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see MainMod
 */
@Command(value = MainMod.MODID, description = "Access the " + MainMod.NAME + " GUI.")
public class ExampleCommand {
    @Main
    private void handle() {
        MainMod.INSTANCE.moduleManager.openGui();
    }
}