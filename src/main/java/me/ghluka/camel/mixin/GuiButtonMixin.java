package me.ghluka.camel.mixin;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import me.ghluka.camel.MainMod;
import me.ghluka.camel.module.config.Font;
import me.ghluka.camel.module.modules.hud.CustomMenu;
import me.ghluka.camel.text.TextSegment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;

@Mixin(GuiButton.class)
public abstract class GuiButtonMixin {
    @Shadow public int xPosition;
    @Shadow public int yPosition;
    @Shadow public int width;
    @Shadow public int height;
    @Shadow public String displayString;
    @Shadow public boolean visible;
    @Shadow public boolean enabled;
    @Shadow public boolean hovered;
    @Shadow public int packedFGColour;

    private final OneColor baseBg = new OneColor(22, 22, 22);
    private final OneColor hoverBg = new OneColor(35, 35, 35);
    private final OneColor disabledBg = new OneColor(15, 15, 15);

    private final OneColor baseText = new OneColor(Color.white);
    private final OneColor hoverText = new OneColor(255, 246, 229);
    private final OneColor disabledText = new OneColor(120, 120, 120);

    /*
    Module: Custom Menu
     */
    @Inject(method = "drawButton", at = @At("HEAD"), cancellable = true)
    private void onDrawButton(Minecraft mc, int mouseX, int mouseY, CallbackInfo ci) {
        CustomMenu cm = (CustomMenu) MainMod.moduleManager.getModuleByName(CustomMenu.MODULE);
        if (cm == null) return;
        if (!cm.getModuleEnabled()) return;
        if (!visible) return;

        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;

        ci.cancel();

        OneColor bg;
        OneColor text;
        if (!enabled) {
            bg = disabledBg;
            text = disabledText;
        } else if (hovered) {
            bg = hoverBg;
            text = hoverText;
        } else {
            bg = baseBg;
            text = baseText;
        }
        if (this.packedFGColour != 0) {
            float red = (float)(packedFGColour >> 16 & 255) / 255.0F;
            float blue = (float)(packedFGColour >> 8 & 255) / 255.0F;
            float green = (float)(packedFGColour & 255) / 255.0F;
            text = new OneColor(new Color(red, blue, green));
        }

        OneColor finalText = text;
        NanoVGHelper.INSTANCE.setupAndDraw(true, (vg) -> {

            if (enabled && hovered) {
                NanoVGHelper.INSTANCE.drawRoundedRect(vg,
                        xPosition, yPosition + 1,
                        width, height - 1,
                        cm.getAccent().getRGB(),
                        6f
                );
            }
            NanoVGHelper.INSTANCE.drawRoundedRect(vg,
                    xPosition, yPosition,
                    width, height - (enabled && hovered ? 1 : 0),
                    bg.getRGB(),
                    6f
            );

            drawMinecraftString(vg,
                    displayString,
                    xPosition + width / 2f,
                    yPosition + height / 2f,
                    finalText,
                    !enabled
            );
        });
    }

    private void drawMinecraftString(long vg, String text, float centerX, float centerY, OneColor defaultColor, boolean disabled) {
        ArrayList<TextSegment> segments = parseFormattedText(text, defaultColor);

        float totalWidth = 0;
        for (TextSegment seg : segments) {
            totalWidth += NanoVGHelper.INSTANCE.getTextWidth(vg, seg.text, 8f, getFont(seg.bold));
        }

        float x = centerX - totalWidth / 2f;
        for (TextSegment seg : segments) {
            int color = disabled ? disabledText.getRGB() : seg.color.getRGB();
            NanoVGHelper.INSTANCE.drawText(vg, seg.text, x, centerY, color, 8f, getFont(seg.bold));
            x += NanoVGHelper.INSTANCE.getTextWidth(vg, seg.text, 8f, getFont(seg.bold));
        }
    }

    private ArrayList<TextSegment> parseFormattedText(String input, OneColor defaultColor) {
        ArrayList<TextSegment> list = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        OneColor color = defaultColor;
        boolean bold = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '§' && i + 1 < input.length()) {
                if (current.length() > 0) {
                    list.add(new TextSegment(current.toString(), color, bold));
                    current.setLength(0);
                }

                char code = input.charAt(i + 1);
                EnumChatFormatting fmt = getFormat(code);
                if (fmt != null) {
                    if (fmt.isColor()) {
                        color = fmt.getColorIndex() >= 0 ? getMCColor(fmt) : defaultColor;
                        bold = false;
                    } else if (fmt == EnumChatFormatting.BOLD) {
                        bold = true;
                    } else if (fmt == EnumChatFormatting.RESET) {
                        color = defaultColor;
                        bold = false;
                    }
                }
                i++;
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            list.add(new TextSegment(current.toString(), color, bold));
        }

        return list;
    }

    private EnumChatFormatting getFormat(char c) {
        switch (c) {
            case '4': return EnumChatFormatting.DARK_RED;
            case 'c': return EnumChatFormatting.RED;
            case '6': return EnumChatFormatting.GOLD;
            case 'e': return EnumChatFormatting.YELLOW;
            case '2': return EnumChatFormatting.DARK_GREEN;
            case 'a': return EnumChatFormatting.GREEN;
            case 'b': return EnumChatFormatting.AQUA;
            case '3': return EnumChatFormatting.DARK_AQUA;
            case '1': return EnumChatFormatting.DARK_BLUE;
            case '9': return EnumChatFormatting.BLUE;
            case 'd': return EnumChatFormatting.LIGHT_PURPLE;
            case '5': return EnumChatFormatting.DARK_PURPLE;
            case 'f': return EnumChatFormatting.WHITE;
            case '7': return EnumChatFormatting.GRAY;
            case '8': return EnumChatFormatting.DARK_GRAY;
            case '0': return EnumChatFormatting.BLACK;
            case 'r': return EnumChatFormatting.RESET;
            case 'l': return EnumChatFormatting.BOLD;
            default: return null;
        }
    }

    private OneColor getMCColor(EnumChatFormatting fmt) {
        switch (fmt) {
            case DARK_RED: return new OneColor(190, 0, 0);
            case RED: return new OneColor(254, 63, 63);
            case GOLD: return new OneColor(217, 163, 52);
            case YELLOW: return new OneColor(254, 254, 63);
            case DARK_GREEN: return new OneColor(0, 190, 0);
            case GREEN: return new OneColor(63, 254, 63);
            case AQUA: return new OneColor(63, 254, 254);
            case DARK_AQUA: return new OneColor(0, 190, 190);
            case DARK_BLUE: return new OneColor(0, 0, 190);
            case BLUE: return new OneColor(63, 63, 254);
            case LIGHT_PURPLE: return new OneColor(254, 63, 254);
            case DARK_PURPLE: return new OneColor(190, 0, 190);
            case GRAY: return new OneColor(190, 190, 190);
            case DARK_GRAY: return new OneColor(63, 63, 63);
            case BLACK: return new OneColor(0, 0, 0);
            default: return new OneColor(255, 255, 255);
        }
    }

    private cc.polyfrost.oneconfig.renderer.font.Font getFont(boolean bold) {
        return Font.Companion.stringToFont(MainMod.moduleManager.font.getFont(), bold);
    }
}
