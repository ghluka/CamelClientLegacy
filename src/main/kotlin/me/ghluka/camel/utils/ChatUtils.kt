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
                put("ogace", EnumChatFormatting.RED.toString() + "[" + EnumChatFormatting.WHITE + "AMBASSADOR" + EnumChatFormatting.RED)
                put("yt", EnumChatFormatting.RED.toString() + "[" + EnumChatFormatting.WHITE + "YOUTUBE" + EnumChatFormatting.RED)
                put("mvppp", EnumChatFormatting.GOLD.toString() + "[MVP" + EnumChatFormatting.RED + "++" + EnumChatFormatting.GOLD)
                put("mvpp", EnumChatFormatting.AQUA.toString() + "[MVP" + EnumChatFormatting.RED + "+" + EnumChatFormatting.AQUA)
                put("mvp", EnumChatFormatting.AQUA.toString() + "[MVP")
                put("vipp", EnumChatFormatting.GREEN.toString() + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN)
                put("vip", EnumChatFormatting.GREEN.toString() + "[VIP")
            }
        }

        val prefix = EnumChatFormatting.LIGHT_PURPLE.toString() + "From "
        val suffix = EnumChatFormatting.GRAY.toString() + ": "

        fun plusColor(rank: String, newPlusColor: EnumChatFormatting): String? {
            return rank.replace(EnumChatFormatting.RED.toString(), newPlusColor.toString())
        }

        val players = arrayOf(
                ranks.get("owner") + "] hypixel",
                ranks.get("owner") + "] Rezzus",
                ranks.get("admin") + "] aPunch",
                ranks.get("admin") + "] Jayavarmen",
                ranks.get("admin") + "] Donpireso",
                ranks.get("admin") + "] TheMGRF",
                ranks.get("admin") + "] LadyBleu",
                ranks.get("admin") + "] Plancke",
                ranks.get("admin") + "] xHascox",
                ranks.get("admin") + "] TimeDeo",
                ranks.get("ogace") + "] TheOriginalAce",
                ranks.get("yt") + "] MenacingBanana",
                ranks.get("yt") + "] HellCastleBTW",
                ranks.get("yt") + "] tylerwith4rs",
                ranks.get("yt") + "] DeathStreeks",
                ranks.get("yt") + "] thirtyvirus",
                ranks.get("yt") + "] HotslicerMC",
                ranks.get("yt") + "] Refraction",
                ranks.get("yt") + "] HsFearless",
                ranks.get("yt") + "] Derailious",
                ranks.get("yt") + "] MashClash",
                ranks.get("yt") + "] Toadstar0",
                ranks.get("yt") + "] fan_19",
                ranks.get("yt") + "] 56ms",
                plusColor(ranks["mvppp"]!!, EnumChatFormatting.DARK_BLUE) + "] 2nfg",
                plusColor(ranks["mvppp"]!!, EnumChatFormatting.DARK_RED) + "] arithemonkey",
                plusColor(ranks["mvpp"]!!, EnumChatFormatting.RED) + "] Minikloon",
                plusColor(ranks["mvpp"]!!, EnumChatFormatting.DARK_BLUE) + "] Dctr",
                plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] Quaglet",
                plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] 50mMidas",
                plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] 15h",
        )
    }
}