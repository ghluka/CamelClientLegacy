package me.ghluka.camel.mixin;

import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import me.ghluka.camel.MainMod;
import me.ghluka.camel.module.config.Font;
import me.ghluka.camel.module.modules.hud.CustomMenu;
import me.ghluka.camel.utils.ShaderUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMainMenu.class, priority = 1983)
public class GuiMainMenuMixin extends GuiScreen {
    private static boolean firstMenuOpened = false;

    /*
    Module: Custom Menu
     */
    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!firstMenuOpened) {
            firstMenuOpened = true; // why shader no work first time :(
            mc.displayGuiScreen(new GuiMainMenu());
            return;
        }
        CustomMenu cm = (CustomMenu) MainMod.moduleManager.getModuleByName(CustomMenu.MODULE);
        if (cm == null) return;
        if (!cm.getModuleEnabled()) return;
        if (! OpenGlHelper.isFramebufferEnabled()) return;

        int rgb = cm.getWallpaper().getRGB();
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        GL11.glClearColor(0f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        initShader();

        GL20.glUseProgram(shaderProgram);
        int colorLocation = GL20.glGetUniformLocation(shaderProgram, "color");
        GL20.glUniform3f(colorLocation, r / 255f, g / 255f, b / 255f);
        int timeLocation = GL20.glGetUniformLocation(shaderProgram, "time");
        GL20.glUniform1f(timeLocation, (System.currentTimeMillis() % 100000L) / 1000f);
        int resolutionLocation = GL20.glGetUniformLocation(shaderProgram, "resolution");
        GL20.glUniform2f(resolutionLocation, width, height);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1, -1);
        GL11.glVertex2f(1, -1);
        GL11.glVertex2f(1, 1);
        GL11.glVertex2f(-1, 1);
        GL11.glEnd();

        GL20.glUseProgram(0);

        super.drawScreen(mouseX, mouseY, partialTicks);
        NanoVGHelper.INSTANCE.setupAndDraw(true, (vg) -> {
            cc.polyfrost.oneconfig.renderer.font.Font normal = getFont(false);
            float size = 8f;

            String cr = "Copyright Mojang AB. Do not distribute!";
            float leftX = 2;
            float leftY = height - size / 2 - 2;
            NanoVGHelper.INSTANCE.drawText(vg, cr, leftX + 1, leftY + 1, 0xFF000000, size, normal);
            NanoVGHelper.INSTANCE.drawText(vg, cr, leftX, leftY, 0xFFFFFFFF, size, normal);

            String line1 = "Camel b" + MainMod.VERSION;
            String line2 = "Loaded " + MainMod.moduleManager.getModules().size() + " modules.";
            float line1Width = NanoVGHelper.INSTANCE.getTextWidth(vg, line1, size, normal);
            float line2Width = NanoVGHelper.INSTANCE.getTextWidth(vg, line2, size, normal);
            float rightX1 = width - line1Width - 2;
            float rightX2 = width - line2Width - 2;
            float rightY2 = leftY - size - 2;
            NanoVGHelper.INSTANCE.drawText(vg, line1, rightX1 + 1, rightY2 + 1, 0xFF000000, size, normal);
            NanoVGHelper.INSTANCE.drawText(vg, line2, rightX2 + 1, leftY + 1, 0xFF000000, size, normal);
            NanoVGHelper.INSTANCE.drawText(vg, line1, rightX1, rightY2, 0xFFFFFFFF, size, normal);
            NanoVGHelper.INSTANCE.drawText(vg, line2, rightX2, leftY, 0xFFFFFFFF, size, normal);

            int accent = cm.getAccent().getRGB();
            String logo = "Camel";
            float logoSize = 32f;
            float logoWidth = NanoVGHelper.INSTANCE.getTextWidth(vg, logo, logoSize, normal);
            float logoX = (width - logoWidth) / 2f;
            float logoY = height / 4f;
            NanoVGHelper.INSTANCE.drawText(vg, logo, logoX + 2, logoY + 2, 0xFF000000, logoSize, normal);
            NanoVGHelper.INSTANCE.drawText(vg, logo, logoX, logoY, accent, logoSize, normal);
        });

        ci.cancel();
    }

    @Inject(method = "renderSkybox", at = @At("HEAD"), cancellable = true)
    public void renderSkybox(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        CustomMenu cm = (CustomMenu) MainMod.moduleManager.getModuleByName(CustomMenu.MODULE);
        if (cm == null) return;
        if (!cm.getModuleEnabled()) return;
        ci.cancel();
    }

    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    public void onInitGui(CallbackInfo ci) {
        CustomMenu cm = (CustomMenu) MainMod.moduleManager.getModuleByName(CustomMenu.MODULE);
        if (cm == null) return;
        if (!cm.getModuleEnabled()) return;

        int j = height / 4 + 54;
        buttonList.add(new GuiButton(1, width / 2 - 103, j, 200, 18, "Campaign"));
        buttonList.add(new GuiButton(2, width / 2 - 103, j + 22, 200, 18, "Multiplayer"));
        buttonList.add(new GuiButton(6, width / 2 - 103, j + 44, 200, 18, "Mods"));
        buttonList.add(new GuiButton(0, width / 2 - 103, j + 66 + 12, 98, 18, "Settings"));
        buttonList.add(new GuiButton(4, width / 2 - 1, j + 66 + 12, 98, 18, "Quit"));

        String ip = cm.getLastServerIP();
        j = height / 4 + 48;
        if (!ip.trim().isEmpty()) {
            buttonList.add(new GuiButton(45678998, width / 2 - 50, j + 112, 100, 20, ip));
        }

        this.mc.setConnectedToRealms(false);
        ci.cancel();
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    protected void onActionPerformed(GuiButton button, CallbackInfo ci) {
        CustomMenu cm = (CustomMenu) MainMod.moduleManager.getModuleByName(CustomMenu.MODULE);
        if (cm == null) return;
        if (!cm.getModuleEnabled()) return;

        String ip = cm.getLastServerIP();
        if (button.id == 45678998 && !ip.trim().isEmpty()) {
            mc.displayGuiScreen(new GuiConnecting(this, mc, new ServerData(ip, ip, false)));
        }
    }

    // Shaders
    private int shaderProgram = -1;

    private void initShader() {
        if (shaderProgram != -1) return;

        String vertSource = ShaderUtils.Companion.loadShader("shader/bg.vert");
        String fragSource = ShaderUtils.Companion.loadShader("shader/bg.frag");

        int vertShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertShader, vertSource);
        GL20.glCompileShader(vertShader);
        if (GL20.glGetShaderi(vertShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
            throw new RuntimeException("Vertex shader compile failed: " + GL20.glGetShaderInfoLog(vertShader, 1024));

        int fragShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragShader, fragSource);
        GL20.glCompileShader(fragShader);
        if (GL20.glGetShaderi(fragShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
            throw new RuntimeException("Fragment shader compile failed: " + GL20.glGetShaderInfoLog(fragShader, 1024));

        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertShader);
        GL20.glAttachShader(shaderProgram, fragShader);
        GL20.glLinkProgram(shaderProgram);
        GL20.glValidateProgram(shaderProgram);
    }

    private cc.polyfrost.oneconfig.renderer.font.Font getFont(boolean bold) {
        return Font.Companion.stringToFont(MainMod.moduleManager.font.getFont(), bold);
    }
}
