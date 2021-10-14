package net.kyrptonaught.kyrptconfig.config.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.AddNonConflictingKeyBind;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.NonConflictingKeyBindData;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.NonConflictingKeyBindEntry;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.NonConflictingKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ControlsListWidget.class)
public abstract class ControlListWidgetMixin {
    @Shadow
    private int maxKeyNameLength;

    @Shadow
    @Final
    private ControlsOptionsScreen parent;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    protected void injectCustomButtons(ControlsOptionsScreen controlsOptionsScreen, MinecraftClient minecraftClient, CallbackInfo ci) {
        List<NonConflictingKeyBindData> keybindings = new ArrayList<>();
        FabricLoader.getInstance().getEntrypoints("registernonconflicting", AddNonConflictingKeyBind.class).forEach(addNonConflictingKeyBind -> addNonConflictingKeyBind.addKeyBinding(keybindings));
        String lastCat = "";
        for (NonConflictingKeyBindData bindData : keybindings) {
            if (!bindData.category.equals(lastCat)) {
                ((EntryListWidget) (Object) this).children().add(new NonConflictingKeyBindEntry.CategoryEntry(new TranslatableText(bindData.category)));
                lastCat = bindData.category;
            }

            NonConflictingKeyBinding keyBinding = new NonConflictingKeyBinding(bindData.name, bindData.inputType, bindData.keyCode, bindData.category, bindData.keySetEvent);
            NonConflictingKeyBindEntry entry = new NonConflictingKeyBindEntry(keyBinding, new TranslatableText(bindData.name), this.parent, this.maxKeyNameLength);
            ((EntryListWidget) (Object) this).children().add(entry);
        }
    }
}