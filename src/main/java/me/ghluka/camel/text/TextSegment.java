package me.ghluka.camel.text;

import cc.polyfrost.oneconfig.config.core.OneColor;

public class TextSegment {
    public String text;
    public OneColor color;
    public boolean bold;

    public TextSegment(String text, OneColor color, boolean bold) {
        this.text = text;
        this.color = color;
        this.bold = bold;
    }
}