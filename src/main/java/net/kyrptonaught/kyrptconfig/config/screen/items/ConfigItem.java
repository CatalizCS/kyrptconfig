package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ConfigItem<T> {
    private Text fieldTitle;
    private List<Text> toolTipText;
    protected Consumer<T> saveConsumer, valueUpdatedEvent;
    protected NotSuckyButton resetButton;
    protected T value, defaultValue;
    private boolean requiresRestart = false;
    private boolean isHidden = false;

    public ConfigItem(Text name, T value, T defaultValue) {
        this.fieldTitle = name;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public ConfigItem<?> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public ConfigItem<?> setValueUpdatedEvent(Consumer<T> valueUpdatedEvent) {
        this.valueUpdatedEvent = valueUpdatedEvent;
        return this;
    }

    public ConfigItem<?> setRequiresRestart() {
        requiresRestart = true;
        ((MutableText) fieldTitle).append(" *");
        return this;
    }

    public ConfigItem<?> setTitleText(Text title) {
        this.fieldTitle = title;
        return this;
    }

    public ConfigItem<?> setToolTipWithNewLine(String translatableKey) {
        String[] translated = Language.getInstance().get(translatableKey).split("\n");
        this.toolTipText = new ArrayList<>();
        for (String line : translated) {
            this.toolTipText.add(Text.literal(line));
        }

        return this;
    }

    public ConfigItem<?> setToolTip(Text toolTip) {
        this.toolTipText = List.of(toolTip);
        return this;
    }

    public ConfigItem<?> setToolTip(Text... toolTips) {
        this.toolTipText = List.of(toolTips);
        return this;
    }

    public ConfigItem<?> setHidden(boolean hidden) {
        this.isHidden = hidden;
        return this;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    protected void runSaveConsumer(T value) {
        if (saveConsumer != null && value != null)
            saveConsumer.accept(value);
    }

    public void save() {
        runSaveConsumer(value);
    }

    public int getSize() {
        if (isHidden) return 0;
        return getHeaderSize() + getContentSize();
    }

    public int getHeaderSize() {
        return 20;
    }

    public int getContentSize() {
        return 0;
    }

    public void useDefaultResetBTN() {
        this.resetButton = new NotSuckyButton(0, 0, 35, 20, Text.translatable("key.kyrptconfig.config.reset"), widget -> {
            resetToDefault();
        });
    }

    public void resetToDefault() {
        setValue(defaultValue);
    }

    public boolean isValueDefault() {
        return value.equals(defaultValue);
    }

    public void setValue(T value) {
        this.value = value;
        if (valueUpdatedEvent != null)
            valueUpdatedEvent.accept(this.value);
    }

    public void tick() {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHidden) return;
        if (resetButton != null)
            resetButton.mouseClicked(mouseX, mouseY, button);
    }

    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;

        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = y + getHeaderSize();
        if (mouseY > y && mouseY < height)
            DrawableHelper.fill(matrices, 0, y - 1, width, height + 1, ColorHelper.Argb.getArgb(255, 55, 55, 55));

        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, this.fieldTitle, x, y + 6, 16777215);
        if (resetButton != null) {
            this.resetButton.setY(y);
            this.resetButton.setX(width - resetButton.getWidth() - 20);
            resetButton.active = !isValueDefault();
            resetButton.render(matrices, mouseX, mouseY, delta);
        }
    }

    public void render2(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;
        if (mouseX > x && mouseX < x + MinecraftClient.getInstance().textRenderer.getWidth(fieldTitle) &&
                mouseY > y && mouseY < y + 12)
            renderToolTip(matrices, mouseX, mouseY);
    }

    public void renderToolTip(MatrixStack matrices, int x, int y) {
        if (toolTipText != null && requiresRestart) {
            List<Text> newList = new ArrayList<>(toolTipText);
            newList.add(Text.translatable("key.kyrptconfig.config.restartRequired"));
            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, newList, x, y);
        } else if (toolTipText != null)
            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, toolTipText, x, y);
        else if (requiresRestart) {
            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, Text.translatable("key.kyrptconfig.config.restartRequired"), x, y);
        }
    }
}
