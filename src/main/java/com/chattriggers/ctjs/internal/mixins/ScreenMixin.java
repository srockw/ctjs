package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.triggers.TriggerType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;extractDeferredElements(IIF)V"
    ))
    private void onScreenRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        TriggerType.RENDER_SCREEN.triggerAll(minecraft.screen, graphics, mouseX, mouseY, a);
    }
}
