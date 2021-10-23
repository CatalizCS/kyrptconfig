package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ButtonItem extends ConfigItem {
    private final NotSuckyButton button;
    Runnable clickEvent;

    public ButtonItem(Text name) {
        super(name, null, null);
        this.button = new NotSuckyButton(0, 0, 100, 20, name, widget -> clickEvent.run());
    }

    public ButtonItem setClickEvent(Runnable clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.button.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(matrices, x, y, mouseX, mouseY, delta);
        this.button.y = y;
        this.button.x = MinecraftClient.getInstance().getWindow().getScaledWidth() - button.getWidth() - 40;

        button.render(matrices, mouseX, mouseY, delta);
    }
}
