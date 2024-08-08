package me.ghluka.camel.utils

import net.minecraft.util.EnumChatFormatting


class ChatUtils {
    companion object {

        var ranks: HashMap<String?, String?> = object : HashMap<String?, String?>() {
            init {
                put("owner", EnumChatFormatting.RED.toString() + "[OWNER")
                put("admin", EnumChatFormatting.RED.toString() + "[ADMIN")
                put("gm", EnumChatFormatting.DARK_GREEN.toString() + "[GM")
                put("techno", EnumChatFormatting.LIGHT_PURPLE.toString() + "[PIG" + EnumChatFormatting.AQUA + "+++" + EnumChatFormatting.LIGHT_PURPLE)
                put("yt", EnumChatFormatting.RED.toString() + "[" + EnumChatFormatting.WHITE + "YOUTUBE" + EnumChatFormatting.RED)
                put("mvppp", EnumChatFormatting.GOLD.toString() + "[MVP" + EnumChatFormatting.RED + "++" + EnumChatFormatting.GOLD)
                put("mvpp", EnumChatFormatting.AQUA.toString() + "[MVP" + EnumChatFormatting.RED + "+" + EnumChatFormatting.AQUA)
                put("mvp", EnumChatFormatting.AQUA.toString() + "[MVP")
                put("vipp", EnumChatFormatting.GREEN.toString() + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN)
                put("vip", EnumChatFormatting.GREEN.toString() + "[VIP")
            }
        }

        var fakeRanks: HashMap<String?, String?> = object : HashMap<String?, String?>() {
            init {
                put("ogace", EnumChatFormatting.RED.toString() + "[" + EnumChatFormatting.WHITE + "AMBASSADOR" + EnumChatFormatting.RED)
            }
        }

        val prefix = EnumChatFormatting.LIGHT_PURPLE.toString() + "From "
        val suffix = EnumChatFormatting.GRAY.toString() + ": "

        fun plusColor(rank: String, newPlusColor: EnumChatFormatting): String? {
            return rank.replace(EnumChatFormatting.RED.toString(), newPlusColor.toString())
        }

        val players = arrayOf(
                ranks["owner"].toString() + "] hypixel",
                ranks["owner"].toString() + "] Rezzus",
                ranks["admin"].toString() + "] Jayavarmen",
                ranks["admin"].toString() + "] Minikloon",
                ranks["admin"].toString() + "] LadyBleu",
                ranks["admin"].toString() + "] Plancke",
                ranks["admin"].toString() + "] xHascox",
                ranks["admin"].toString() + "] TimeDeo",
                fakeRanks["ogace"].toString() + "] TheOriginalAce",
                ranks["yt"].toString() + "] MenacingBanana",
                ranks["yt"].toString() + "] HellCastleBTW",
                ranks["yt"].toString() + "] tylerwith4rs",
                ranks["yt"].toString() + "] DeathStreeks",
                ranks["yt"].toString() + "] thirtyvirus",
                ranks["yt"].toString() + "] HotslicerMC",
                ranks["yt"].toString() + "] Refraction",
                ranks["yt"].toString() + "] HsFearless",
                ranks["yt"].toString() + "] Derailious",
                ranks["yt"].toString() + "] MashClash",
                ranks["yt"].toString() + "] Toadstar0",
                ranks["yt"].toString() + "] fan_19",
                ranks["yt"].toString() + "] 56ms",
                ranks["yt"].toString() + "] 15h",
                plusColor(ranks["mvppp"]!!, EnumChatFormatting.DARK_BLUE) + "] 2nfg",
                plusColor(ranks["mvppp"]!!, EnumChatFormatting.DARK_RED) + "] arithemonkey",
                plusColor(ranks["mvpp"]!!, EnumChatFormatting.DARK_BLUE) + "] Dctr",
                plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] Quaglet"
        )
    }
}