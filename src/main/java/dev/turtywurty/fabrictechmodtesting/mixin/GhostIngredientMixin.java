package dev.turtywurty.fabrictechmodtesting.mixin;

import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GhostRecipe.GhostIngredient.class)
public abstract class GhostIngredientMixin {
    @Shadow
    @Final
    private Ingredient ingredient;

    @Shadow(aliases = {"field_3085"})
    @Final
    private GhostRecipe ghostRecipe;

    @Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
    private void fabrictechmodtesting$getItem(CallbackInfoReturnable<ItemStack> callback) {
        CustomIngredient customIngredient = this.ingredient.getCustomIngredient();
        if(customIngredient instanceof CountedIngredient countedIngredient) {
            List<ItemStack> itemStacks = countedIngredient.getMatchingStacks();
            callback.setReturnValue(itemStacks.isEmpty() ? ItemStack.EMPTY : itemStacks.get(Mth.floor(ghostRecipe.time / 30.0F) % itemStacks.size()));
        }
    }
}
