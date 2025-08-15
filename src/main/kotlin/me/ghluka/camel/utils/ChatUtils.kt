package me.ghluka.camel.utils

import net.minecraft.util.EnumChatFormatting


class ChatUtils {
    companion object {

        var ranks: HashMap<String?, String?> = object : HashMap<String?, String?>() {
            init {
                put("staff", "${EnumChatFormatting.RED}[${EnumChatFormatting.GOLD}ዞ${EnumChatFormatting.RED}")
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
            ranks.get("staff") + "] hypixel",
            ranks.get("staff") + "] Rezzus",
            ranks.get("staff") + "] aPunch",
            ranks.get("staff") + "] Jayavarmen",
            ranks.get("staff") + "] Donpireso",
            ranks.get("staff") + "] TheMGRF",
            ranks.get("staff") + "] LadyBleu",
            ranks.get("staff") + "] Plancke",
            ranks.get("staff") + "] xHascox",
            ranks.get("staff") + "] TimeDeo",
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
            ranks.get("yt") + "] 56ms",
            plusColor(ranks["mvppp"]!!, EnumChatFormatting.BLACK) + "] fan_19",
            plusColor(ranks["mvppp"]!!, EnumChatFormatting.DARK_BLUE) + "] 2nfg",
            plusColor(ranks["mvppp"]!!, EnumChatFormatting.BLACK) + "] 15h",
            plusColor(ranks["mvppp"]!!, EnumChatFormatting.DARK_RED) + "] arithemonkey",
            plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] Mynetrix",
            plusColor(ranks["mvpp"]!!, EnumChatFormatting.RED) + "] Minikloon",
            plusColor(ranks["mvpp"]!!, EnumChatFormatting.DARK_BLUE) + "] Dctr",
            plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] Quaglet",
            plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] BenC1ark",
            plusColor(ranks["mvpp"]!!, EnumChatFormatting.BLACK) + "] 50mMidas",
            "${EnumChatFormatting.GRAY}Jimero",
        )
    }
}