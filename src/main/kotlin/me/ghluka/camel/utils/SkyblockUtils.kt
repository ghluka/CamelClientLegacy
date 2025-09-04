package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.scoreboard.Score
import net.minecraft.util.EnumChatFormatting


class SkyblockUtils {
    companion object {
        fun isInGarden(): Boolean {
            return hasLine("Copper")
        }

        fun hasScoreboardTitle(title: String?): Boolean {
            return mc.thePlayer != null && mc.thePlayer.worldScoreboard != null && mc.thePlayer.worldScoreboard
                .getObjectiveInDisplaySlot(1) != null && ChatFormatting.stripFormatting(
                mc.thePlayer.worldScoreboard.getObjectiveInDisplaySlot(1).displayName
            ).equals(title, ignoreCase = true)
        }

        fun hasLine(line: String): Boolean {
            if (mc.thePlayer != null && mc.thePlayer.worldScoreboard != null && mc.thePlayer.worldScoreboard.getObjectiveInDisplaySlot(1) != null) {
                val sb = Minecraft.getMinecraft().thePlayer.worldScoreboard
                val list: MutableList<Score> = ArrayList<Score>(sb.getSortedScores(sb.getObjectiveInDisplaySlot(1)))

                for (score in list) {
                    val team = sb.getPlayersTeam(score.playerName)
                    if (team == null) continue

                    val s = ChatFormatting.stripFormatting(team.colorPrefix + score.playerName + team.colorSuffix)

                    val builder = StringBuilder()
                    for (c in s.toCharArray()) {
                        if (c.code < 256) builder.append(c)
                    }
                    if (builder.toString().lowercase().contains(line.lowercase())) {
                        return true
                    }
                }
            }
            return false
        }

        fun getPing(): Int {
            val info = mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID)
            return info?.responseTime ?: 0
        }

        private const val NBT_TYPE_STRING = 8

        fun loreLines(stack: ItemStack?, stripFormatting: Boolean = true): List<String> {
            if (stack == null || !stack.hasTagCompound()) return emptyList()
            val display = stack.tagCompound?.getCompoundTag("display") ?: return emptyList()
            val loreList = display.getTagList("Lore", NBT_TYPE_STRING) ?: return emptyList()

            val lines = ArrayList<String>(loreList.tagCount())
            for (i in 0 until loreList.tagCount()) {
                val raw = loreList.getStringTagAt(i)
                val line = if (stripFormatting)
                    EnumChatFormatting.getTextWithoutFormattingCodes(raw) ?: raw
                else
                    raw
                lines.add(line)
            }
            return lines
        }

        fun loreContains(stack: ItemStack?, needle: String, ignoreCase: Boolean = true, stripFormatting: Boolean = true): Boolean {
            if (needle.isEmpty()) return false
            return loreLines(stack, stripFormatting).any { it.contains(needle, ignoreCase) }
        }
    }
}