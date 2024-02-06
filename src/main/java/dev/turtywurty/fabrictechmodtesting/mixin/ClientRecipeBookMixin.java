package dev.turtywurty.fabrictechmodtesting.mixin;

import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {
    @Inject(method = "getCategory",
            at = @At(value = "HEAD", target = "Lnet/minecraft/world/item/crafting/RecipeHolder;value()Lnet/minecraft/world/item/crafting/Recipe;"),
            cancellable = true)
    private static void fabrictechmodtesting$getCategory(RecipeHolder<?> recipeHolder, CallbackInfoReturnable<RecipeBookCategories> callback) {
        if (recipeHolder.value() instanceof AlloyFurnaceRecipe) {
            callback.setReturnValue(RecipeBookCategories.valueOf("ALLOY_FURNACE"));
        }
    }
}
