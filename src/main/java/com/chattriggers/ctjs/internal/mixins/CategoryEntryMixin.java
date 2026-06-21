package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.CustomKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyBindsList.CategoryEntry.class)
public class CategoryEntryMixin {
    @Redirect(method = "<init>", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/KeyMapping$Category;label()Lnet/minecraft/network/chat/Component;"
    ))
    private Component replaceCustomCategory(KeyMapping.Category category) {
        var name = CustomKeyMapping.getCategories().get(category);
        return name == null ? category.label() : Component.literal(name);
    }
}
