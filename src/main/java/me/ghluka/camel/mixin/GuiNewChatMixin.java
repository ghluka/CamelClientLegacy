package me.ghluka.camel.mixin;

import me.ghluka.camel.MainMod;
import me.ghluka.camel.module.modules.hypixel.skyblock.PSTWaypoint;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

import static me.ghluka.camel.MainMod.mc;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin {
    private HashMap<String, String> map = new HashMap<String, String>() {{
        put("PRECURSOR_CITY", "internal_city");
        put("JUNGLE_TEMPLE", "internal_temple");
        put("GOBLIN_QUEEN", "internal_den");
        //put("", "internal_mines");
        put("GOBLIN_KING", "internal_king");
        put("BAL", "internal_bal");
        //put("", "internal_fairy");
        put("CORLEONE", "internal_corleone");
    }};

    /*
    Module: PST Waypoint
     */
    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private void onPrintChatMessage(IChatComponent chatComponent, int chatLineId, CallbackInfo ci) {
        final PSTWaypoint pst = (PSTWaypoint) MainMod.moduleManager.getModuleByName(PSTWaypoint.MODULE);
        String unformatted = chatComponent.getUnformattedText();
        // PizzaClient > §aFOUND STRUCTURE GOBLIN_KING-A - 449 120 671
        if (pst != null && pst.getModuleEnabled() &&
                unformatted.startsWith("PizzaClient") && unformatted.contains("FOUND STRUCTURE")) {
            String waypoint = "";
            if (pst.getMiscWaypoints()) {
                waypoint = unformatted.split("FOUND STRUCTURE ")[1].split(" ")[0];
                waypoint = waypoint.substring(0, waypoint.length() - 2);
                waypoint = waypoint.replaceAll("-", " ");
                waypoint = WordUtils.capitalizeFully(waypoint);
            }
            for (String key : map.keySet()) {
                if (unformatted.contains(key)) {
                    waypoint = map.get(key);
                    break;
                }
            }
            if (!waypoint.isEmpty()) {
                String[] parts = unformatted.split(" ");
                int x = Integer.parseInt(parts[parts.length - 3]);
                int y = Integer.parseInt(parts[parts.length - 2]);
                int z = Integer.parseInt(parts[parts.length - 1]);

                ClientCommandHandler.instance.executeCommand(mc.thePlayer,
                        "/sthw set '" + waypoint + "' " + x + " " + y + " " + z);
            }
        }
    }
}