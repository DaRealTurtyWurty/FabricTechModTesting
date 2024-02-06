package dev.turtywurty.fabrictechmodtesting.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    @ModifyExpressionValue(
            method = "updateTabs",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookTabButton;getCategory()Lnet/minecraft/client/RecipeBookCategories;"
            )
    )
    private RecipeBookCategories fabrictechmodtesting$updateTabs(RecipeBookCategories original) {
        if (original == RecipeBookCategories.valueOf("ALLOY_FURNACE")) {
            return RecipeBookCategories.CRAFTING_SEARCH;
        }

        return original;
    }
}
