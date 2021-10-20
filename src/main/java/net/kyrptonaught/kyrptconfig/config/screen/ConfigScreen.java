package net.kyrptonaught.kyrptconfig.config.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    int selectedSection = 0, sectionRenderY = 0;
    List<ConfigSection> sections = new ArrayList<>();
    private Runnable saveRunnable;
    Screen previousScreen;

    public ConfigScreen(Screen previousScreen, Text title) {
        super(title);
        this.previousScreen = previousScreen;
    }

    protected void init() {
        int center = this.width / 2;
        this.addDrawableChild(new NotSuckyButton(center - 153, height - 25, 150, 20, new TranslatableText("key.kyrptconfig.config.exit"), widget -> {
            this.client.setScreen(previousScreen);
        }));

        this.addDrawableChild(new NotSuckyButton(center + 3, height - 25, 150, 20, new TranslatableText("key.kyrptconfig.config.saveExit"), widget -> {
            for (ConfigSection section : sections) {
                section.save();
            }
            saveRunnable.run();
            this.client.setScreen(previousScreen);
        }));
        for (ConfigSection section : sections) {
            section.init(client, width, height - 55 - 30);
        }
    }

    public void setSavingEvent(Runnable save) {
        this.saveRunnable = save;
    }

    public void addConfigSection(ConfigSection item) {
        item.selectionIndex = sections.size();
        if (sections.size() == 0)
            item.sectionSelectionBTN.x = 10;
        else
            item.sectionSelectionBTN.x = sections.get(sections.size() - 1).sectionSelectionBTN.x + sections.get(sections.size() - 1).sectionSelectionBTN.getWidth() + 3;
        item.sectionSelectionBTN.setWidth(MinecraftClient.getInstance().textRenderer.getWidth(item.title) + 10);

        this.sections.add(item);
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigSection section : sections) {
            section.tick();
        }
    }

    public void setSelectedSection(int selectedSection) {
        this.selectedSection = selectedSection;
        sectionRenderY = 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        return sections.get(selectedSection).keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return sections.get(selectedSection).charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        for (ConfigSection section : sections)
            if (section.sectionSelectionBTN.mouseClicked(mouseX, mouseY, button)) return true;
        return sections.get(selectedSection).mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        sectionRenderY = MathHelper.clamp(sectionRenderY + (int) (amount * 10), -calculateSectionHeight(), 0);
        return true;
    }

    public int calculateSectionHeight() {
        int visibleHeight = this.height - 55 - 30;
        int sectionSize = sections.get(selectedSection).getTotalSectionSize();
        if (sectionSize <= visibleHeight) return 0;
        return sectionSize - visibleHeight;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(0);
        fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);

        sections.get(selectedSection).render(matrices, sectionRenderY + 55, mouseX, mouseY, delta);

        renderBackgroundTexture(0, 55, 100);
        renderBackgroundTexture(this.height - 30, 30, 100);

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 0xffffff);
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).sectionSelectionBTN.active = i != selectedSection;
            sections.get(i).sectionSelectionBTN.render(matrices, mouseX, mouseY, delta);
        }
        sections.get(selectedSection).render2(matrices, sectionRenderY + 55, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void renderBackgroundTexture(int startY, int size, int vOffset) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D, (double) startY + size, 0.0D).texture(0.0F, (float) size / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, (double) startY + size, 0.0D).texture((float) this.width / 32.0F, (float) size / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, startY, 0.0D).texture((float) this.width / 32.0F, (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0D, startY, 0.0D).texture(0.0F, (float) vOffset).color(64, 64, 64, 255).next();
        tessellator.draw();
    }
}
