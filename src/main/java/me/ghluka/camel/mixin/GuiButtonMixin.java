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

    private final OneColor baseBg = new OneColor(22, 22, 22);
    private final OneColor hoverBg = new OneColor(35, 35, 35);
    private final OneColor disabledBg = new OneColor(15, 15, 15);

    private final OneColor baseText = new OneColor(Color.white);
    private final OneColor disabledText = new OneColor(120, 120, 120);

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
            text = baseText;
        } else {
            bg = baseBg;
            text = baseText;
        }

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
                    text.getRGB(),
                    !enabled
            );
        });
    }

    private void drawMinecraftString(long vg, String text, float centerX, float centerY, int defaultColor, boolean disabled) {
        ArrayList<TextSegment> segments = parseFormattedText(text, defaultColor);

        float totalWidth = 0;
        for (TextSegment seg : segments) {
            totalWidth += NanoVGHelper.INSTANCE.getTextWidth(vg, seg.text, 8f, getFont(seg.bold));
        }

        float x = centerX - totalWidth / 2f;
        for (TextSegment seg : segments) {
            int color = disabled ? disabledText.getRGB() : seg.color;
            NanoVGHelper.INSTANCE.drawText(vg, seg.text, x, centerY, color, 8f, getFont(seg.bold));
            x += NanoVGHelper.INSTANCE.getTextWidth(vg, seg.text, 8f, getFont(seg.bold));
        }
    }

    private ArrayList<TextSegment> parseFormattedText(String input, int defaultColor) {
        ArrayList<TextSegment> list = new ArrayList<TextSegment>();
        StringBuilder current = new StringBuilder();
        int color = defaultColor;
        boolean bold = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '§' && i + 1 < input.length()) {
                if (current.length() > 0) {
                    list.add(new TextSegment(current.toString(), color, bold));
                    current.setLength(0);
                }

                char code = input.charAt(i + 1);
                EnumChatFormatting fmt = EnumChatFormatting.func_175744_a(code);
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

    private int getMCColor(EnumChatFormatting fmt) {
        switch (fmt) {
            case BLACK: return 0x000000;
            case DARK_BLUE: return 0x0000AA;
            case DARK_GREEN: return 0x00AA00;
            case DARK_AQUA: return 0x00AAAA;
            case DARK_RED: return 0xAA0000;
            case DARK_PURPLE: return 0xAA00AA;
            case GOLD: return 0xFFAA00;
            case GRAY: return 0xAAAAAA;
            case DARK_GRAY: return 0x555555;
            case BLUE: return 0x5555FF;
            case GREEN: return 0x55FF55;
            case AQUA: return 0x55FFFF;
            case RED: return 0xFF5555;
            case LIGHT_PURPLE: return 0xFF55FF;
            case YELLOW: return 0xFFFF55;
            default: return 0xFFFFFF;
        }
    }

    private cc.polyfrost.oneconfig.renderer.font.Font getFont(boolean bold) {
        return Font.Companion.stringToFont(MainMod.moduleManager.font.getFont(), bold);
    }
}
