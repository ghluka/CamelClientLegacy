package me.ghluka.camel.text;

public class TextSegment {
    public String text;
    public int color;
    public boolean bold;

    public TextSegment(String text, int color, boolean bold) {
        this.text = text;
        this.color = color;
        this.bold = bold;
    }
}